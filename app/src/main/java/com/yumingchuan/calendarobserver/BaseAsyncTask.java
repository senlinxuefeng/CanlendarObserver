//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yumingchuan.calendarobserver;

import android.content.Context;
import android.os.AsyncTask;

public abstract class BaseAsyncTask<T> extends AsyncTask<Void, Void, T> {
    protected Context mContext;
    protected OnTaskFinishedListener<T> mOnTaskFinishedListener;

    public BaseAsyncTask(Context context, OnTaskFinishedListener<T> onTaskFinishedListener) {
        this.mContext = context;
        this.mOnTaskFinishedListener = onTaskFinishedListener;
    }

    public BaseAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected abstract T doInBackground(Void... var1);

    @Override
    protected void onPostExecute(T data) {
        super.onPostExecute(data);
        if (this.mOnTaskFinishedListener != null) {
            this.mOnTaskFinishedListener.onTaskFinished(data);
        }

    }
}
