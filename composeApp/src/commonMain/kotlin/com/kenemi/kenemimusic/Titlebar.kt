package com.kenemi.kenemimusic

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.input.pointer.pointerInput
import org.jetbrains.compose.resources.painterResource
import kenemimusic.composeapp.generated.resources.Res
import kenemimusic.composeapp.generated.resources.km_icon
import androidx.compose.foundation.Image

// =====================================================
// BARRE DE TITRE CUSTOM
// =====================================================

@Composable
fun WindowScope.CustomTitleBar(
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
    isMaximized: Boolean = false
) {
    WindowDraggableArea {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color(0xFF000000)) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Logo KM ──
            Image(
                painter = painterResource(Res.drawable.km_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp)
            )

            // ── Titre centré ──
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Kenemi Music",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.3.sp
                )
            }

            // ── Boutons de contrôle ──
            Row(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimize
                TitleBarButton(
                    onClick = onMinimize,
                    hoverColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height(1.5.dp)
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                }

                // Maximize / Restore
                TitleBarButton(
                    onClick = onMaximize,
                    hoverColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (isMaximized) {
                        // Icône restore (deux carrés)
                        Box(modifier = Modifier.size(10.dp)) {
                            Box(modifier = Modifier.size(7.dp).offset(3.dp, 0.dp)
                                .border(1.5.dp, MaterialTheme.colorScheme.onSurface,
                                    RoundedCornerShape(1.dp)))
                            Box(modifier = Modifier.size(7.dp).offset(0.dp, 3.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.5.dp, MaterialTheme.colorScheme.onSurface,
                                    RoundedCornerShape(1.dp)))
                        }
                    } else {
                        Box(modifier = Modifier.size(10.dp)
                            .border(1.5.dp, MaterialTheme.colorScheme.onSurface,
                                RoundedCornerShape(1.dp)))
                    }
                }

                // Close
                TitleBarButton(
                    onClick = onClose,
                    hoverColor = Color(0xFFE81123)
                ) {
                    Box(modifier = Modifier.size(10.dp), contentAlignment = Alignment.Center) {
                        // X dessiné avec deux boîtes rotées
                        Icon(
                            imageVector = Icons.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun TitleBarButton(
    onClick: () -> Unit,
    hoverColor: Color,
    content: @Composable BoxScope.() -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHovered) hoverColor else Color.Transparent)
            .clickable { onClick() }
            .onHover { isHovered = it },
        contentAlignment = Alignment.Center,
        content = content
    )
}

// Extension pour détecter le hover
fun Modifier.onHover(onHover: (Boolean) -> Unit): Modifier =
    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    androidx.compose.ui.input.pointer.PointerEventType.Enter -> onHover(true)
                    androidx.compose.ui.input.pointer.PointerEventType.Exit -> onHover(false)
                    else -> {}
                }
            }
        }
    }