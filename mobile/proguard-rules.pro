#Wire
-keep class com.squareup.wire.** { *; }
-keep class com.yourcompany.yourgeneratedcode.** { *; }

#Moshi
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

#paho rules
-keepclasseswithmembers class org.eclipse.paho.** { *; }
-keepclasseswithmembernames class org.eclipse.paho.** { *; }
-keep class org.eclipse.paho.** { *; }

#Kodein
-keepattributes Signature

#Kotlin
-keepclassmembers class **$WhenMappings {
    <fields>;
}

#Koltin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# Retrofit 2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

