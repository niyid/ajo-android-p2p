# Add project specific ProGuard rules here.

# Monero JNI
-keep class com.m2049r.xmrwallet.model.** { *; }
-keep class com.m2049r.xmrwallet.data.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Parcelize
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.ajo.monero.**$serializer { *; }
-keepclassmembers class com.ajo.monero.** {
    *** Companion;
}
-keepclasseswithmembers class com.ajo.monero.** {
    kotlinx.serialization.KSerializer serializer(...);
}
