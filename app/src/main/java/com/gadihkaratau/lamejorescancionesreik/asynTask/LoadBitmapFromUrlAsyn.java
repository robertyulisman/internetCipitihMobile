package com.gadihkaratau.lamejorescancionesreik.asynTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadBitmapUrlAsyncCallback;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;

import java.lang.ref.WeakReference;

public class LoadBitmapFromUrlAsyn extends AsyncTask<String, Integer, Bitmap> {

    private WeakReference<LoadBitmapUrlAsyncCallback> loadBitmapUrlAsyncCallbackWeakReference;

    public LoadBitmapFromUrlAsyn(LoadBitmapUrlAsyncCallback loadBitmapUrlAsyncCallback) {
        loadBitmapUrlAsyncCallbackWeakReference = new WeakReference<>(loadBitmapUrlAsyncCallback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadBitmapUrlAsyncCallbackWeakReference.get().preExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        return Tools.getBitmapFromURL(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        loadBitmapUrlAsyncCallbackWeakReference.get().postExecute(bitmap);
    }
}
