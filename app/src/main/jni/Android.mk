LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog -ljnigraphics
LOCAL_MODULE := toBitmap
LOCAL_SRC_FILES := org_k_JNIUtils.cpp
include $(BUILD_SHARED_LIBRARY)
