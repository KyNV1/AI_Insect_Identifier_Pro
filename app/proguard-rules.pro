# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers (not the raw source file name) so Crashlytics stack traces
# stay readable after minification.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===================================================================
# Our own data models & Room entities — field names must survive so
# manual org.json parsing (InsectInfo.fromJson) and Room's generated
# DAO code keep matching the compiled class shape.
# ===================================================================
-keep class com.kynv1.aiinsectidentifierpro.data.model.** { *; }
-keep class com.kynv1.aiinsectidentifierpro.data.local.entity.** { *; }

# ===================================================================
# Google Generative AI (Gemini SDK) and Play Billing — keep their model
# classes intact rather than assume their consumer rules cover everything.
# ===================================================================
-keep class com.google.ai.client.generativeai.** { *; }
-keep class com.android.billingclient.api.** { *; }

# ===================================================================
# kotlinx.coroutines references some optional/debug-only classes that
# aren't on the runtime classpath; without this R8 fails the build over
# missing classes that are never actually loaded.
# ===================================================================
-dontwarn kotlinx.coroutines.**

# Room, Hilt/Dagger, Firebase, and Coil each ship their own consumer
# ProGuard rules inside their AARs (auto-merged by AGP), so we don't
# duplicate blanket -keep rules for them here — that would just bloat
# the APK back up and defeat the point of enabling R8.

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
