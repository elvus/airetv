# Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Retrofit
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Gson - Keep data models
-keep class com.airetv.app.data.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
