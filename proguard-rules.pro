-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keepnames class org.apache.* {*;}
-keep public class org.apache.* {*;}
-dontwarn org.apache.commons.logging.LogFactory
-dontwarn org.apache.http.annotation.ThreadSafe
-dontwarn org.apache.http.annotation.Immutable
-dontwarn org.apache.http.annotation.NotThreadSafe
-dontwarn com.dimaslanjaka.**
-dontwarn nu.studer.java.**
 # Keep - Library. Keep all public and protected classes, fields, and methods.
-keep public class * {
    public protected <fields>;
    public protected <methods>;
}