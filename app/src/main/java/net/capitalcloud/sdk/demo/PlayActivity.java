package net.capitalcloud.sdk.demo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import net.capitalcloud.sdk.demo.util.MyLogger;

import java.util.ArrayList;
import java.util.Formatter;

import net.capitalcloud.live.sdk.impl.SimplePlayerProperty;
import net.capitalcloud.live.sdk.util.SDKConstants;
import net.capitalcloud.live.sdk.view.VideoView;

public class PlayActivity extends Activity
{
    private VideoView mVideoView;
    private TextView mProgressView;
    private ProgressReceiver mProgressRecevier;
    private ArrayList<SimplePlayerProperty> mPlayerList;
    private String url;
    private String title;

    private static final String LOG_TAG = PlayActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_play_video);

            mProgressView = (TextView) findViewById(R.id.display_progress);
            String text = getString(R.string.current_play_progress, formatTime(0));
            mProgressView.setText(text);
            initControllView();

            Intent intent = getIntent();
            this.url  = intent.getStringExtra(SDKConstants.LIVE_URL);
            this.title = intent.getStringExtra(SDKConstants.KEY_TITLE);
            initPlayer();
            registerReceiver();
        } catch (Exception e) {
            MyLogger.w(LOG_TAG, "Exception:", e);
        }
    }

    public static void gotoPlay(Context context, String rtmpOUrl,
                                String thumbImgUrl, String title, String liveId) {
        if (!TextUtils.isEmpty(rtmpOUrl)) {
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra(SDKConstants.LIVE_URL, rtmpOUrl);
            intent.putExtra(SDKConstants.THUMB_IMG, thumbImgUrl);
            intent.putExtra(SDKConstants.KEY_TITLE, title);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "播放地址为空", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressRecevier != null)
            unregisterReceiver(mProgressRecevier);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            if (mVideoView != null) {
                if (mVideoView.rotatedFromBtn()) {
                } else {
                    if (mVideoView.isFullScreen()) {
                        mVideoView.toMiniScreen();
                    } else {
                        mVideoView.toFullScreen();
                    }
                }
            }
        } catch (Exception e) {
            MyLogger.w(LOG_TAG, "onConfigurationChanged exception");
        }
    }

    private void registerReceiver() {
        mProgressRecevier = new ProgressReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKConstants.ACTION_PLAY_PROGRESS_CHANGED);
        registerReceiver(mProgressRecevier, filter);
    }


    private class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SDKConstants.ACTION_PLAY_PROGRESS_CHANGED == action) {
                String id = intent.getStringExtra(SDKConstants.KEY_ID);
                String title = intent.getStringExtra(SDKConstants.KEY_TITLE);
                float progress = intent.getFloatExtra(SDKConstants.KEY_PROGRESS, 0);
                String text = getString(R.string.current_play_progress, formatTime(progress));
                mProgressView.setText(text);
            }

        }
    }

    private String formatTime(float time) {
        if (time < 60) {
            return time + "";
        }

        int seconds = (int) (time % 60);
        int minutes = (int) ((time % 3600) / 60);
        int hours = (int) (time / 3600);

        Formatter formatter = new Formatter();
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static class DefinitionDialog extends DialogFragment {
        private int index = 0;

        public static DefinitionDialog newInstance(String title, String[] definitions) {
            DefinitionDialog definitionDialog = new DefinitionDialog();
            Bundle bundle = new Bundle(2);
            bundle.putString("title", title);
            bundle.putStringArray("definitions", definitions);
            definitionDialog.setArguments(bundle);
            return definitionDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString("title");
            final String[] definitions = getArguments().getStringArray("definitions");

            return new AlertDialog.Builder(getActivity()).setTitle(title)
                    .setSingleChoiceItems(definitions, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            index = which;
                        }
                    }).setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                MyLogger.w(LOG_TAG, "fragment_download exception", e);
                            }

                        }
                    }).create();

        }
    }

    private void initPlayer() {
        mPlayerList = new ArrayList<SimplePlayerProperty>();
        play();
    }

    private void play() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.playVideo(this.url,this.title);
    }

    public void initControllView() {
        Button playBtn = (Button) findViewById(R.id.btn_play);
        playBtn.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_play:
                    mVideoView.startOrPause();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null)
            mVideoView.onActivityPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null)
            mVideoView.onActivityResume();

    }

}

