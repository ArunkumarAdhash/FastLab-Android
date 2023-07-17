# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions

# Gson specific classes
#-keep class com.lifehope.ResponseModel.SettingsResponse { *; }
#-keepclassmembernames class com.lifehope.ResponseModel.SettingsResponse { <fields>; }


-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-dontwarn sun.misc.**

#Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }


#Okhttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

-keep class com.lifehopehealthapp.ResponseModel.** { *; }

#Google Lib
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#AndroidX
-keep class com.google.android.material.** { *; }

-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-keep class androidx.paging.PagedStorage.init



#Facebook
#-keep class com.facebook.** {
#   *;
#}
#-keep class com.facebook.android.*
#-keep class android.webkit.WebViewClient
#-keep class * extends android.webkit.WebViewClient
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    <methods>;
#}

#Google+
-dontwarn com.google.**.R
-dontwarn com.google.**.R$*
-dontnote

-keep public class com.google.android.gms.analytics.**, com.google.android.gms.common.**, com.google.android.gms.location.** {
    public protected *;
}

-keep class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    java.lang.String NULL;
}

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#Log
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
  public static *** v(...);
  public static *** d(...);
  public static *** i(...);
  public static *** w(...);
  public static *** e(...);
}


#AWS
-keep class org.apache.commons.logging.**               { *; }
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class org.codehaus.**                             { *; }
-keepattributes Signature,*Annotation*

-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**

-dontwarn com.amazonaws.util.json.**

-keepnames class com.amazonaws.**
-keepnames class com.amazon.**

-keep class com.amazonaws.services.**.*Handler

-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**

-dontwarn org.apache.http.**

-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**