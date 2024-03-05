package com.krmao.screenshot;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public interface CaptureCallback {
    void invoke(@Nullable Bitmap data);
}
