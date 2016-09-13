package net.capitalcloud.sdk.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import net.capitalcloud.live.sdk.SDKClient;
import net.capitalcloud.live.sdk.callback.ResponseListener;
import net.capitalcloud.sdk.demo.util.LocalConstants;

public class VideoService extends Service {
    private SDKClient mClient = null;
    private LocalBinder myBinder = new LocalBinder();

    private final File STORAGE_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/demo");

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mClient = SDKClient.getInstance(getApplication(), LocalConstants.TOKEN);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mClient != null) {
            mClient.release();
            mClient = null;
        }

        myBinder = null;
    }

    public class LocalBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }

    private SDKClient getClient() {
        return mClient == null ? mClient.getInstance(this, LocalConstants.TOKEN) : mClient;
    }

    public void getLiveList(ResponseListener responseListener) {
        mClient.getLiveManager().list(responseListener);
    }
}
