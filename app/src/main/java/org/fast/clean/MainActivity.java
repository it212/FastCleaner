package org.fast.clean;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.fast.clean.bean.SDCardInfo;
import org.fast.clean.bean.StorageUtil;
import org.fast.clean.utils.AppUtil;
import org.fast.clean.view.circleprogress.ArcProgress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArcProgress mArcStore;

    private ArcProgress mArcProcess;

    private TextView mCapacity;

    private TextView mTvJunk;

    private TextView mTvPhoto;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }

        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.initData();
    }

    private void initView() {
        mArcStore = (ArcProgress) findViewById(R.id.arc_store);
        mArcProcess = (ArcProgress) findViewById(R.id.arc_process);
        mCapacity = (TextView) findViewById(R.id.capacity);
        mTvJunk = (TextView) findViewById(R.id.tv_junk);
        mTvPhoto = (TextView) findViewById(R.id.tv_photo);
        mTvPhoto.setOnClickListener(this);
        mTvJunk.setOnClickListener(this);
    }

    private void initData() {
        long l = AppUtil.getAvailMemory(this);
        long y = AppUtil.getTotalMemory(this);
        final double x = (((y - l) / (double) y) * 100);
        mArcProcess.setProgress((int) x);

//        mArcProcess.setProgress(0);


        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(this);

        long nAvailaBlock;
        long TotalBlocks;
        if (mSDCardInfo != null) {
            nAvailaBlock = mSDCardInfo.free + mSystemInfo.free;
            TotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            nAvailaBlock = mSystemInfo.free;
            TotalBlocks = mSystemInfo.total;
        }

        final double percentStore = (((TotalBlocks - nAvailaBlock) / (double) TotalBlocks) * 100);

        mCapacity.setText(StorageUtil.convertStorage(TotalBlocks - nAvailaBlock) + "/" + StorageUtil.convertStorage(TotalBlocks));
        mArcStore.setProgress((int) percentStore);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_junk:
                break;
            case R.id.tv_photo:
                startActivity(new Intent(this, SimilarPhotoActivity.class));
                break;
        }
    }
}