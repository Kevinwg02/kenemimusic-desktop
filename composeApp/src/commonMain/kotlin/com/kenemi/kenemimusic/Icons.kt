package com.kenemi.kenemimusic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// =====================================================
// ICÔNES VECTORIELLES CENTRALISÉES
// =====================================================

object Icons {

    val Player: ImageVector get() = imageVector("Player") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            // Cercle
            moveTo(12f, 12f)
            arcTo(10f, 10f, 0f, true, true, 11.999f, 12f)
            close()
        }
        path(
            fill = SolidColor(Color.White),
            pathFillType = PathFillType.NonZero
        ) {
            // Triangle play
            moveTo(10f, 8.5f)
            lineTo(15.5f, 12f)
            lineTo(10f, 15.5f)
            close()
        }
    }

    val Songs: ImageVector get() = imageVector("Songs") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(4f, 6f); lineTo(20f, 6f)
            moveTo(4f, 12f); lineTo(20f, 12f)
            moveTo(4f, 18f); lineTo(14f, 18f)
        }
    }

    val Artists: ImageVector get() = imageVector("Artists") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Tête
            moveTo(12f, 8f)
            arcTo(4f, 4f, 0f, true, true, 11.999f, 8f)
            close()
            // Corps
            moveTo(4f, 20f)
            arcTo(8f, 8f, 0f, false, true, 20f, 20f)
        }
    }

    val Albums: ImageVector get() = imageVector("Albums") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Vinyle
            moveTo(12f, 12f)
            arcTo(10f, 10f, 0f, true, true, 11.999f, 12f)
            close()
            moveTo(12f, 12f)
            arcTo(3f, 3f, 0f, true, true, 11.999f, 12f)
            close()
        }
    }

    val Playlists: ImageVector get() = imageVector("Playlists") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(4f, 6f); lineTo(20f, 6f)
            moveTo(4f, 12f); lineTo(20f, 12f)
            moveTo(4f, 18f); lineTo(20f, 18f)
        }
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(17f, 14f); lineTo(21f, 17f); lineTo(17f, 20f); close()
        }
    }

    val Stats: ImageVector get() = imageVector("Stats") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 18f)
            lineTo(7f, 12f)
            lineTo(11f, 15f)
            lineTo(15f, 7f)
            lineTo(21f, 7f)
        }
    }

    val Settings: ImageVector get() = imageVector("Settings") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Engrenage simplifié
            moveTo(12f, 15f)
            arcTo(3f, 3f, 0f, true, true, 11.999f, 15f)
            close()
            moveTo(19.4f, 15f)
            arcTo(1.65f, 1.65f, 0f, false, false, 0.33f, 1f)
            lineTo(21f, 12f)
            arcTo(1.65f, 1.65f, 0f, false, false, 0.32f, -1f)
            lineTo(20f, 9f)
        }
    }

    // Icônes de contrôle du lecteur
    val Play: ImageVector get() = imageVector("Play") {
        path(fill = SolidColor(Color.White), pathFillType = PathFillType.NonZero) {
            moveTo(8f, 5f); lineTo(19f, 12f); lineTo(8f, 19f); close()
        }
    }

    val Pause: ImageVector get() = imageVector("Pause") {
        path(fill = SolidColor(Color.White), pathFillType = PathFillType.NonZero) {
            moveTo(6f, 4f); lineTo(10f, 4f); lineTo(10f, 20f); lineTo(6f, 20f); close()
            moveTo(14f, 4f); lineTo(18f, 4f); lineTo(18f, 20f); lineTo(14f, 20f); close()
        }
    }

    val Previous: ImageVector get() = imageVector("Previous") {
        path(fill = SolidColor(Color.White), pathFillType = PathFillType.NonZero) {
            moveTo(16f, 5f); lineTo(7f, 12f); lineTo(16f, 19f); close()
        }
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(5f, 5f); lineTo(5f, 19f)
        }
    }

    val Next: ImageVector get() = imageVector("Next") {
        path(fill = SolidColor(Color.White), pathFillType = PathFillType.NonZero) {
            moveTo(8f, 5f); lineTo(17f, 12f); lineTo(8f, 19f); close()
        }
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(19f, 5f); lineTo(19f, 19f)
        }
    }

    val Heart: ImageVector get() = imageVector("Heart") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 21f)
            curveTo(12f, 21f, 3f, 15f, 3f, 9f)
            arcTo(5f, 5f, 0f, false, true, 12f, 7.5f)
            arcTo(5f, 5f, 0f, false, true, 21f, 9f)
            curveTo(21f, 15f, 12f, 21f, 12f, 21f)
            close()
        }
    }

    val HeartFilled: ImageVector get() = imageVector("HeartFilled") {
        path(
            fill = SolidColor(KenemiColors.Favorite),
            strokeLineWidth = 0f
        ) {
            moveTo(12f, 21f)
            curveTo(12f, 21f, 3f, 15f, 3f, 9f)
            arcTo(5f, 5f, 0f, false, true, 12f, 7.5f)
            arcTo(5f, 5f, 0f, false, true, 21f, 9f)
            curveTo(21f, 15f, 12f, 21f, 12f, 21f)
            close()
        }
    }

    val Lyrics: ImageVector get() = imageVector("Lyrics") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(4f, 6f); lineTo(20f, 6f)
            moveTo(4f, 10f); lineTo(20f, 10f)
            moveTo(4f, 14f); lineTo(14f, 14f)
        }
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(16f, 16f); lineTo(20f, 19f); lineTo(16f, 22f)
        }
    }

    val Shuffle: ImageVector get() = imageVector("Shuffle") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(16f, 3f); lineTo(21f, 3f); lineTo(21f, 8f)
            moveTo(4f, 20f); lineTo(21f, 3f)
            moveTo(21f, 16f); lineTo(21f, 21f); lineTo(16f, 21f)
            moveTo(15f, 15f); lineTo(21f, 21f)
            moveTo(4f, 4f); lineTo(9f, 9f)
        }
    }

    val Repeat: ImageVector get() = imageVector("Repeat") {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(17f, 1f); lineTo(21f, 5f); lineTo(17f, 9f)
            moveTo(3f, 11f); lineTo(3f, 9f); arcTo(8f, 8f, 0f, false, true, 21f, 9f)
            moveTo(7f, 23f); lineTo(3f, 19f); lineTo(7f, 15f)
            moveTo(21f, 13f); lineTo(21f, 15f); arcTo(8f, 8f, 0f, false, true, 3f, 15f)
        }
    }
    val Close: ImageVector get() = ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19f, 6.41f); lineTo(17.59f, 5f); lineTo(12f, 10.59f)
            lineTo(6.41f, 5f); lineTo(5f, 6.41f); lineTo(10.59f, 12f)
            lineTo(5f, 17.59f); lineTo(6.41f, 19f); lineTo(12f, 13.41f)
            lineTo(17.59f, 19f); lineTo(19f, 17.59f); lineTo(13.41f, 12f); close()
        }
    }.build()

    val DragHandle: ImageVector get() = ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(3f, 15f); horizontalLineToRelative(18f); verticalLineToRelative(-2f)
            horizontalLineTo(3f); close()
            moveTo(3f, 11f); horizontalLineToRelative(18f); verticalLineToRelative(-2f)
            horizontalLineTo(3f); close()
        }
    }.build()

    val VolumeDown: ImageVector get() = ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.5f, 12f)
            curveTo(18.5f, 10.23f, 17.48f, 8.71f, 16f, 7.97f)
            verticalLineTo(16.02f)
            curveTo(17.48f, 15.29f, 18.5f, 13.77f, 18.5f, 12f)
            close()
            moveTo(5f, 9f)
            verticalLineTo(15f)
            horizontalLineTo(9f)
            lineTo(14f, 20f)
            verticalLineTo(4f)
            lineTo(9f, 9f)
            horizontalLineTo(5f)
            close()
        }
    }.build()

    val VolumeUp: ImageVector get() = ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(3f, 9f)
            verticalLineTo(15f)
            horizontalLineTo(7f)
            lineTo(12f, 20f)
            verticalLineTo(4f)
            lineTo(7f, 9f)
            horizontalLineTo(3f)
            close()
            moveTo(16.5f, 12f)
            curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
            verticalLineTo(16.02f)
            curveTo(15.48f, 15.29f, 16.5f, 13.77f, 16.5f, 12f)
            close()
            moveTo(14f, 3.23f)
            verticalLineTo(5.29f)
            curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
            curveTo(19f, 15.17f, 16.89f, 17.85f, 14f, 18.71f)
            verticalLineTo(20.77f)
            curveTo(18.01f, 19.86f, 21f, 16.28f, 21f, 12f)
            curveTo(21f, 7.72f, 18.01f, 4.14f, 14f, 3.23f)
            close()
        }
    }.build()

    val VolumeMute: ImageVector get() = ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(16.5f, 12f)
            curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
            verticalLineTo(11.18f)
            lineTo(16.45f, 13.63f)
            curveTo(16.48f, 13.1f, 16.5f, 12.55f, 16.5f, 12f)
            close()
            moveTo(19f, 12f)
            curveTo(19f, 12.94f, 18.8f, 13.82f, 18.46f, 14.64f)
            lineTo(19.97f, 16.15f)
            curveTo(20.62f, 14.91f, 21f, 13.5f, 21f, 12f)
            curveTo(21f, 7.72f, 18.01f, 4.14f, 14f, 3.23f)
            verticalLineTo(5.29f)
            curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
            close()
            moveTo(4.27f, 3f)
            lineTo(3f, 4.27f)
            lineTo(7.73f, 9f)
            horizontalLineTo(3f)
            verticalLineTo(15f)
            horizontalLineTo(7f)
            lineTo(12f, 20f)
            verticalLineTo(13.27f)
            lineTo(16.25f, 17.52f)
            curveTo(15.58f, 18.04f, 14.83f, 18.45f, 14f, 18.7f)
            verticalLineTo(20.76f)
            curveTo(15.38f, 20.45f, 16.63f, 19.82f, 17.68f, 18.96f)
            lineTo(19.73f, 21f)
            lineTo(21f, 19.73f)
            lineTo(12f, 10.73f)
            lineTo(4.27f, 3f)
            close()
            moveTo(12f, 4f)
            lineTo(9.91f, 6.09f)
            lineTo(12f, 8.18f)
            verticalLineTo(4f)
            close()
        }
    }.build()

    val Search: ImageVector get() = ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.5f, 14f)
            horizontalLineTo(14.71f)
            lineTo(14.43f, 13.73f)
            curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
            curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
            curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f)
            curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f)
            curveTo(11.11f, 16f, 12.59f, 15.41f, 13.73f, 14.43f)
            lineTo(14f, 14.71f)
            verticalLineTo(15.5f)
            lineTo(19f, 20.49f)
            lineTo(20.49f, 19f)
            lineTo(15.5f, 14f)
            close()
            moveTo(9.5f, 14f)
            curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
            curveTo(5f, 7.01f, 7.01f, 5f, 9.5f, 5f)
            curveTo(11.99f, 5f, 14f, 7.01f, 14f, 9.5f)
            curveTo(14f, 11.99f, 11.99f, 14f, 9.5f, 14f)
            close()
        }
    }.build()
}

// =====================================================
// HELPER pour créer un ImageVector facilement
// =====================================================

private fun imageVector(
    name: String,
    block: androidx.compose.ui.graphics.vector.ImageVector.Builder.() -> Unit
): ImageVector {
    return ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply(block).build()
}

