package com.convenience.store.scanner.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeScannerWithOverlay(
    onBarcodeDetected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BarcodeScannerScreen(onBarcodeDetected = onBarcodeDetected)

        ScannerOverlay()
    }
}

@Composable
fun ScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // 1. Calcoliamo le dimensioni del mirino (es. 70% della larghezza, proporzionale)
        val scannerWidth = size.width * 0.7f
        val scannerHeight = scannerWidth * 0.6f // Un po' più basso che largo

        // Centro dello schermo
        val center = Offset(size.width / 2f, size.height / 2f)

        // Coordinate del rettangolo di scansione
        val scannerRect = Rect(
            center = center,
            radius = scannerWidth / 2f
        ).let {
            // Aggiustiamo l'altezza separatamente
            Rect(it.left, center.y - scannerHeight / 2f, it.right, center.y + scannerHeight / 2f)
        }

        // 2. Disegniamo lo sfondo semitrasparente (Oscuramento)
        drawRect(
            color = Color.Black.copy(alpha = 0.6f),
            size = size
        )

        // 3. Il "Trucco": Ritagliamo il rettangolo centrale
        // Disegniamo un rettangolo arrotondato usando BlendMode.Clear
        // Questo renderà l'area trasparente, mostrando la fotocamera sotto.
        drawRoundRect(
            color = Color.Transparent, // Il colore non importa con Clear
            topLeft = scannerRect.topLeft,
            size = scannerRect.size,
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        // 4. (Opzionale) Disegniamo un bordo bianco sottile attorno al foro
        drawRoundRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = scannerRect.topLeft,
            size = scannerRect.size,
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
    }
}