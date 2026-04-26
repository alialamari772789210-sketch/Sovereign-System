#!/bin/sh

# ==========================================
# AEGIS TACTICAL - SOVEREIGN BUILD WRAPPER
# ARCHITECT: COLONEL ALI AL-AMMARI
# VERSION: 7.2.6
# ==========================================

# تحديد مسار العمل الحالي
APP_HOME=$(pwd)

# تحديد الفواصل بناءً على نظام التشغيل لضمان التخفي التقني
case "$(uname)" in
  CYGWIN* | mingw* | MSYS*)
    sep=';'
    ;;
  *)
    sep=':'
    ;;
esac

# تحديد مكان "نواة البناء" (Gradle Wrapper)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# إعطاء أمر التنفيذ المباشر لبدء تجميع الـ APK
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
