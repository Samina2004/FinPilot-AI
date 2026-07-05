package com.finpilotai.data.local.ocr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class TextRecognitionHelper @Inject constructor() {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(imagePath: String): Result<String> =
        suspendCancellableCoroutine { continuation ->
            try {
                // ── FIX 1: Rotation correct karo ──────────────────────
                val bitmap = loadCorrectlyOrientedBitmap(imagePath)
                    ?: run {
                        continuation.resume(Result.failure(Exception("Could not load image")))
                        return@suspendCancellableCoroutine
                    }

                val image = InputImage.fromBitmap(bitmap, 0)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        continuation.resume(Result.success(visionText.text))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(Result.failure(e))
                    }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resumeWithException(e)
            }
        }

    // ── Rotation fix helper ────────────────────────────────────────────
    private fun loadCorrectlyOrientedBitmap(imagePath: String): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(imagePath) ?: return null
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90  -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL   -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap // rotation fix fail ho to original return karo
        }
    }
}