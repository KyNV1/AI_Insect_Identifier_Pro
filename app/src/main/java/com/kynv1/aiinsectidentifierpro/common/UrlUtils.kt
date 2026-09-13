package com.kynv1.aiinsectidentifierpro.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.kynv1.aiinsectidentifierpro.R

/**
 * Opens [url] in the system browser. Falls back to a toast when the device has no
 * app able to handle the intent, so the tap never fails silently.
 */
fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, context.getString(R.string.error_no_browser), Toast.LENGTH_SHORT).show()
    }
}
