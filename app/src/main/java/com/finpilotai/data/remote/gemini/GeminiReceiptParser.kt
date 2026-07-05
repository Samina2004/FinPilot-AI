package com.finpilotai.data.remote.gemini

import com.finpilotai.BuildConfig
import com.finpilotai.domain.model.ExpenseCategory
import com.google.ai.client.generativeai.GenerativeModel
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiReceiptParser @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun parseReceiptText(rawText: String): Result<ParsedReceipt> {
        return try {
            val prompt = buildPrompt(rawText)
            val response = model.generateContent(prompt)
            val responseText = response.text ?: return Result.success(fallbackParse(rawText))

            val cleanJson = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleanJson)
            val parsed = ParsedReceipt(
                merchant = json.optString("merchant", "Unknown"),
                amount   = json.optDouble("amount", 0.0),
                category = json.optString("category", ExpenseCategory.OTHER)
            )
            Result.success(parsed)
        } catch (e: Exception) {
            // Gemini fail ho gaya (quota/network/parsing) — local fallback use karo
            Result.success(fallbackParse(rawText))
        }
    }

    private fun buildPrompt(rawText: String): String {
        return """
        You are a receipt parser for Pakistani receipts. Extract the final bill total.
        
        RULES:
        - merchant: Look for restaurant/store NAME only — skip addresses, phone numbers, locations like city names
        - amount: FINAL TOTAL as a complete decimal number (e.g. 2299.50 NOT just 50) — pick the largest "total" value, never split on decimal point
        - If amount has commas like "2,299.50" treat it as 2299.50
        - category: pick ONE from this list only: ${ExpenseCategory.all.joinToString(", ")}
        
        Receipt text:
        $rawText
        
        Return ONLY this JSON, no markdown, no explanation:
        {"merchant": "name", "amount": number, "category": "category"}
    """.trimIndent()
    }

    // ── Fallback: simple rule-based parsing (no AI needed) ────────────────────
    private fun fallbackParse(rawText: String): ParsedReceipt {
        val amount = extractAmount(rawText)
        val merchant = extractMerchant(rawText)
        val category = guessCategory(rawText)
        return ParsedReceipt(merchant = merchant, amount = amount, category = category)
    }

    private fun extractAmount(text: String): Double {
        val lines = text.lines()

        val totalKeywords = listOf(
            "grand total", "net total", "total amount",
            "amount due", "total due", "bill total",
            "total rs", "total pkr", "total rupees",
            "total:", "total"
        )

        // ── Neeche se upar scan karo — total aksar end mein hota hai ──────
        for (keyword in totalKeywords) {
            for (line in lines.reversed()) {
                val lower = line.lowercase()
                if (lower.contains(keyword)) {
                    // Decimal numbers bhi pakdo: 2299.50, 1,500.00
                    val number = Regex("[0-9]{1,6}[,]?[0-9]*[.]?[0-9]{0,2}")
                        .findAll(line)
                        .mapNotNull {
                            it.value
                                .replace(",", "")
                                .toDoubleOrNull()
                        }
                        .filter { it > 100 } // 50, 10 jaise ignore karo
                        .maxOrNull()
                    if (number != null) return number
                }
            }
        }

        // ── Last 5 lines mein dhundo ───────────────────────────────────────
        for (line in lines.takeLast(5).reversed()) {
            val number = Regex("[0-9]{1,6}[,]?[0-9]*[.]?[0-9]{0,2}")
                .findAll(line)
                .mapNotNull { it.value.replace(",", "").toDoubleOrNull() }
                .filter { it > 100 }
                .maxOrNull()
            if (number != null) return number
        }

        return 0.0
    }

    private fun extractMerchant(text: String): String {
        val lines = text.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // Skip karo agar line address/location lagti ho
        val skipKeywords = listOf(
            "street", "road", "avenue", "block", "sector",
            "phase", "plaza", "floor", "shop", "near",
            "karachi", "lahore", "islamabad", "rawalpindi",
            "tel", "phone", "mobile", "www", "http",
            "receipt", "invoice", "bill", "cash memo",
            "thank you", "thanks", "visit", "welcome"
        )

        for (line in lines.take(6)) { // pehli 6 lines mein dhundo
            val lower = line.lowercase()
            val isAddress = skipKeywords.any { lower.contains(it) }
            val hasOnlyNumbers = line.all { it.isDigit() || it.isWhitespace() }

            if (!isAddress && !hasOnlyNumbers && line.length > 2) {
                return line.take(40)
            }
        }

        return lines.firstOrNull()?.take(40) ?: "Unknown"
    }
    private fun guessCategory(text: String): String {
        val lower = text.lowercase()
        return when {
            listOf("restaurant", "food", "cafe", "burger", "pizza", "mcdonald", "kfc", "biryani").any { lower.contains(it) } -> ExpenseCategory.FOOD
            listOf("uber", "careem", "petrol", "fuel", "taxi", "bus", "transport").any { lower.contains(it) } -> ExpenseCategory.TRANSPORT
            listOf("mart", "store", "shop", "mall", "fashion", "clothing").any { lower.contains(it) } -> ExpenseCategory.SHOPPING
            listOf("electricity", "bill", "internet", "wifi", "gas", "utility").any { lower.contains(it) } -> ExpenseCategory.BILLS
            listOf("cinema", "movie", "netflix", "game").any { lower.contains(it) } -> ExpenseCategory.ENTERTAINMENT
            listOf("pharmacy", "hospital", "clinic", "medicine", "doctor").any { lower.contains(it) } -> ExpenseCategory.HEALTH
            listOf("school", "university", "book", "course", "fee").any { lower.contains(it) } -> ExpenseCategory.EDUCATION
            else -> ExpenseCategory.OTHER
        }
    }
}

data class ParsedReceipt(
    val merchant: String,
    val amount: Double,
    val category: String
)