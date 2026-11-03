package com.kynv1.aiinsectidentifierpro.common

import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object ShareHelper {

    suspend fun buildShareIntent(context: Context, insect: InsectEntity): Intent {
        val storeLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val text = context.getString(
            R.string.share_result_text,
            insect.commonName,
            insect.scientificName,
            insect.dangerLevel,
            storeLink
        )
        val imageUri = copyImageForSharing(context, insect.imageUri)
        val send = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            if (imageUri != null) {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                clipData = ClipData.newRawUri(null, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
        }
        AnalyticsHelper.logShareResult(hasImage = imageUri != null)
        return Intent.createChooser(send, context.getString(R.string.share_result_chooser_title))
    }

    // Copied into our cache: a gallery Uri can lose its read grant after an app restart, and other
    // apps may only read our files through the FileProvider. Bundled drawables share text only.
    private suspend fun copyImageForSharing(context: Context, source: String): Uri? =
        withContext(Dispatchers.IO) {
            if (!source.startsWith(ContentResolver.SCHEME_CONTENT)) return@withContext null
            try {
                val target = File(File(context.cacheDir, "shared").apply { mkdirs() }, "share.jpg")
                val copied = context.contentResolver.openInputStream(Uri.parse(source))?.use { input ->
                    target.outputStream().use { input.copyTo(it) }
                }
                if (copied == null) return@withContext null
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", target)
            } catch (e: Exception) {
                Timber.w(e, "Could not attach the insect photo, sharing text only")
                null
            }
        }
}
