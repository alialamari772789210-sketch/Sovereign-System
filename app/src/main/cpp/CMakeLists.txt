cmake_minimum_required(VERSION 3.10.2)

# مشروع الحماية السيادية - AEGIS
project("Aegis_Sovereign_Security")

# إعدادات السرعة القصوى (C++17) لمعالجة بيانات الأقمار الصناعية
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# بناء المكتبة الأمنية المحصنة
add_library( 
             aegis-security-lib
             SHARED
             native-lib.cpp
           )

# استدعاء مكتبات النظام (الرصد والعتاد)
find_library( log-lib log )
find_library( android-lib android )

# الربط التكاملي بين النواة والعتاد
target_link_libraries( 
                       aegis-security-lib
                       ${log-lib}
                       ${android-lib}
                     )

# بروتوكول التخفي (إخفاء الرموز البرمجية لمنع الهندسة العكسية)
set_target_properties(aegis-security-lib PROPERTIES 
                      CXX_VISIBILITY_PRESET hidden
                      VISIBILITY_INLINES_HIDDEN ON)
