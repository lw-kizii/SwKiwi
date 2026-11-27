cmake_minimum_required(VERSION 3.4.1)

project("kiwi")

add_library(kiwi SHARED
        main.c

        # Core
        core/log.h

        # Modding Framework!
        framework/framework.c
        framework/hooks.c
)

target_include_directories(
        ${CMAKE_PROJECT_NAME} PRIVATE

        ${CMAKE_CURRENT_SOURCE_DIR}/core
        ${CMAKE_CURRENT_SOURCE_DIR}/framework
        ${CMAKE_CURRENT_SOURCE_DIR}/libs
)

add_library(GlossHook SHARED IMPORTED)
set_target_properties(
        GlossHook
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libGlossHook.so
)

target_link_libraries(kiwi
        GlossHook
	    log
        android
)
