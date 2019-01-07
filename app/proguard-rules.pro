# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/act/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-dontnote sun.misc.Unsafe
-dontnote com.google.gson.internal.UnsafeAllocator

# OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontnote okhttp3.**

### Retrofit 2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
## Retrofit 2.1.0, OkHttp3
#-keepclasseswithmembers class * { #https://github.com/square/retrofit/issues/2129
#    @retrofit2.http.* <methods>;
#}
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$** # https://github.com/square/retrofit/issues/2034
-dontwarn okio.**
-dontwarn okhttp3.**
#To use Single instead of Observable in Retrofit interface
-keepnames class rx.Single

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-dontwarn rx.internal.**
-dontnote rx.internal.**

# Guava
-dontwarn java.lang.ClassValue
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Google
-keep class com.google.**
-dontwarn com.google.**
-keep class com.google.api.** { *; }
-dontwarn com.google.api.**
-keep class com.google.api.services.people.v1.model.** { *; }
-dontwarn com.google.api.services.people.v1.model.**

# MPAndoridChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

# Fomes Model
-keep class com.formakers.fomes.model.** { *; }
-dontwarn com.formakers.fomes.model.**

-keep class com.formakers.fomes.common.network.vo.** { *; }
-dontwarn com.formakers.fomes.common.network.vo.**

-keep class com.formakers.fomes.common.repository.model.** { *; }
-dontwarn com.formakers.fomes.common.repository.model.**

# Constants
-keepattributes InnerClasses
-keep class com.formakers.fomes.common.FomesConstants.** { *; }
-keep class com.formakers.fomes.common.FomesConstants.$* { *; }
-dontwarn com.formakers.fomes.common.FomesConstants.**