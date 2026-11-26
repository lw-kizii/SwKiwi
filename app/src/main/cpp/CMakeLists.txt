cmake_minimum_required(VERSION 3.4.1)

project("kiwi")

add_library(kiwi SHARED
	main.c
)

target_link_libraries(kiwi
	log
	android
)
