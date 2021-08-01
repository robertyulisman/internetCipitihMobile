package com.gadihkaratau.lamejorescancionesreik.interfaces;

import android.graphics.Bitmap;

public interface LoadBitmapUrlAsyncCallback {
    void preExecute();

    void postExecute(Bitmap bitmap);
}
