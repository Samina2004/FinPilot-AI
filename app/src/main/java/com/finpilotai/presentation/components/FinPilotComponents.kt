// ============================================================
// FILE: presentation/components/FinPilotComponents.kt
// ============================================================
package com.finpilotai.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.finpilotai.ui.theme.*

// ── Glass Card container (matches "glass-card" from HTML design) ──────────────
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        SurfaceContainerLow.copy(alpha = 0.9f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = OutlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        content = content
    )
}

// ── Primary Button ────────────────────────────────────────────────────────────
@Composable
fun FinPilotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor   = OnPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text  = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)
                )
            )
        }
    }
}

// ── Outlined Secondary Button ─────────────────────────────────────────────────
@Composable
fun FinPilotOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape    = RoundedCornerShape(12.dp),
        border   = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            // brush uses outline variant color
        ),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = OnBackground)
    ) {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

// ── Email Text Field ──────────────────────────────────────────────────────────
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Email address") },
            isError       = isError,
            singleLine    = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction    = imeAction
            ),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            shape  = RoundedCornerShape(12.dp),
            colors = finPilotTextFieldColors()
        )
        AnimatedVisibility(visible = isError && errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text  = errorMessage ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

// ── Password Text Field ───────────────────────────────────────────────────────
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text(label) },
            isError       = isError,
            singleLine    = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = imeAction
            ),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            shape  = RoundedCornerShape(12.dp),
            colors = finPilotTextFieldColors()
        )
        AnimatedVisibility(visible = isError && errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text  = errorMessage ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

// ── Shared TextField Colors ───────────────────────────────────────────────────
@Composable
fun finPilotTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Primary,
    unfocusedBorderColor = OutlineVariant,
    errorBorderColor     = Error,
    focusedLabelColor    = Primary,
    cursorColor          = Primary,
    focusedContainerColor   = SurfaceContainerLowest,
    unfocusedContainerColor = SurfaceContainerLowest,
)

// ── Or Divider ────────────────────────────────────────────────────────────────
@Composable
fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
        Text(
            text     = "  or  ",
            style    = MaterialTheme.typography.labelMedium,
            color    = OnSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
    }
}

// ── Error Snackbar State Helper ───────────────────────────────────────────────
@Composable
fun FinPilotSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState) { data ->
        Snackbar(
            snackbarData    = data,
            shape           = RoundedCornerShape(12.dp),
            containerColor  = InverseSurface,
            contentColor    = InverseOnSurface,
            actionColor     = PrimaryFixed
        )
    }
}
