# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
set(libs "${CMAKE_SOURCE_DIR}/libs")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${CMAKE_SOURCE_DIR}/src/main/include)

add_library(three SHARED IMPORTED )
set_target_properties(three PROPERTIES IMPORTED_LOCATION
        ${libs}/${ANDROID_ABI}/libthree.so)

add_library(whale SHARED IMPORTED )
set_target_properties(whale PROPERTIES IMPORTED_LOCATION
        ${libs}/${ANDROID_ABI}/libwhale.so)

add_library(sandhook SHARED IMPORTED )
set_target_properties(sandhook PROPERTIES IMPORTED_LOCATION
        ${libs}/${ANDROID_ABI}/libsandhook.so)

add_library(sandhooknative SHARED IMPORTED )
set_target_properties(sandhook PROPERTIES IMPORTED_LOCATION
        ${libs}/${ANDROID_ABI}/libsandhook-native.so)

add_library( # Sets the name of the library.
        myc

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${CMAKE_SOURCE_DIR}/src/main/cpp/one/native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_include_directories(myc PRIVATE
        ${CMAKE_SOURCE_DIR}/src/main/include)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(myc android three whale sandhook log
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )