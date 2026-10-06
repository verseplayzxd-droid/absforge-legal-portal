package com.absforge.ui.animation

import android.graphics.BitmapFactory
import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgeSurfaceElevated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val imageCache = LruCache<String, ImageBitmap>(40)

@Composable
fun ExerciseThumbnail(
    animationId: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    cornerRadius: Int = 12
) {
    val context = LocalContext.current
    val assetPath = remember(animationId) { ExerciseMediaRegistry.getImageAssetPath(animationId) }

    val imageBitmapState = produceState<ImageBitmap?>(initialValue = assetPath?.let { imageCache[it] }, key1 = assetPath) {
        if (assetPath == null) {
            value = null
            return@produceState
        }

        val cached = imageCache[assetPath]
        if (cached != null) {
            value = cached
            return@produceState
        }

        value = withContext(Dispatchers.IO) {
            try {
                context.assets.open(assetPath).use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val imgBitmap = bitmap?.asImageBitmap()
                    if (imgBitmap != null) {
                        imageCache.put(assetPath, imgBitmap)
                    }
                    imgBitmap
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(Color(0xFF141615)),
        contentAlignment = Alignment.Center
    ) {
        val bitmap = imageBitmapState.value
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = AbsForgePrimary,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}
