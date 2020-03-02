package com.zx.pg;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD=0;//message what
    private Boolean mHasQuit = false;

    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap=new ConcurrentHashMap<>();
//、、存取和请求的关联的下载URL，以标记下载请求的T作为key

    private Handler mResponseHandler;//存放主线程的handler
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;//响应请求

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);//显示UI更新
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {//新的构造方法
        super(TAG);
        mResponseHandler = responseHandler;
    }
    public ThumbnailDownloader() {
        super(TAG);}

        @Override
        public boolean quit () {
            mHasQuit = true;
            return super.quit();
    }
    public  void queueThumbnail(T target,String url){//T标志具体哪些次下载
        Log.i(TAG, "queueThumbnail: Got a url:"+url);
        if (url == null) {
            mRequestMap.remove(target);//mRequestMap 存取和请求关联的下载链接
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)//从线程池获取message(what,obj) 这里target是viewholder这个消息，
                    .sendToTarget();//自动设置目标handler，发送给handler

/*message/ what int obj 用户指定的对象 target 处理消息的hangdler*/


            // 获取和发送消息
        }


    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {//消息的名称
                    T target = (T) msg.obj;//消息的对象
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }
    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);//获取下载链接

            if (url == null) {
                return;
            }

            byte[] bitmapBytes = new PhotoFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (mRequestMap.get(target) != url ||
                            mHasQuit) {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}
