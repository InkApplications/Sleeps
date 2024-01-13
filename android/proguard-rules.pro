# Kotlin
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.
-keep public class kotlinx.serialization.* { public *; }
-dontwarn kotlinx.serialization.**
-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}
