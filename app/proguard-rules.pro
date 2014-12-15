# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/AndroidSDK/tools/proguard/proguard-android.txt
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

# appcompat v7
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep public class * extends android.support.v7.app.ActionBarActivity { *; }
-keep class android.support.v7.widget.** { *; }

# butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

# retrofit
-keep class retrofit.** { *; }

# gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# morannon
#-keep class net.alopix.morannon.** { *; }

# net.alopix
-keep class net.alopix.util.** { *; }

# okhttp
-keep class com.squareup.okhttp.** { *; }

# flow
-keep class flow.** { *; }

# circular-progress-button
-keep class com.dd.** { *; }

# rippleeffect
-keep class com.andexert.library.** { *; }
