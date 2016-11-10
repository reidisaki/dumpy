-keepattributes Signature
-keepclassmembers enum * { *; }
-keepattributes *Annotation*
-keep class com.yoenko.areyouthereyet.update.** { *; }
-keepattributes SourceFile,LineNumberTable
# rename the source files to something meaningless, but it must be retained
-renamesourcefileattribute ''
