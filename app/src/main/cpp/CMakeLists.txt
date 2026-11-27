cmake_minimum_required(VERSION 3.4.1)

project("kiwi")

add_library(kiwi SHARED
        main.c

        # Modding Framework!
        framework/framework.c
        framework/hooks.c
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
