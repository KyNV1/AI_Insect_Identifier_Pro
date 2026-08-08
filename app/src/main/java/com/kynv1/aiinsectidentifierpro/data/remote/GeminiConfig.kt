package com.kynv1.aiinsectidentifierpro.data.remote

import com.kynv1.aiinsectidentifierpro.BuildConfig

object GeminiConfig {
    // Injected from GEMINI_API_KEY in local.properties (gitignored). Never hardcode the key here.
    var API_KEY: String = BuildConfig.GEMINI_API_KEY
}
