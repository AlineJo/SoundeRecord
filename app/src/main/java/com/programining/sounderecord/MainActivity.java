package com.programining.sounderecord;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_REQUEST = 100;
    private static final String LOG_TAG = "MainActivity";

    private Button btnRecord;
    private Button btnStopRecord;
    private Button btnPlay;
    private Button btnStopPlay;

    private MediaRecorder mMediaRecorder;
    private String mFileName;
    private MediaPlayer mPlayer;
    private boolean isPlaying;
    private boolean isRecording;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRecord = findViewById(R.id.record);
        btnStopRecord = findViewById(R.id.stop_recording);
        btnPlay = findViewById(R.id.play);
        btnStopPlay = findViewById(R.id.stop_playing);

        btnStopRecord.setEnabled(false);
        btnPlay.setEnabled(false);
        btnStopPlay.setEnabled(false);

        if (mFileName != null) {
            btnPlay.setEnabled(true);
        }

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionsGranted()) {
                    startRecording();
                } else {
                    showRunTimePermission();
                }
            }
        });
        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlaying();
            }
        });
        btnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
            }
        });


    }


    private void startRecording() {

        setFileName();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setOutputFile(mFileName);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed " + e.toString());
        }
        mMediaRecorder.start();

        isRecording = true;
        btnRecord.setEnabled(false);
        btnStopRecord.setEnabled(true);
        btnPlay.setEnabled(false);
        btnStopPlay.setEnabled(false);
    }

    private void stopRecording() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;

        isRecording = false;
        btnRecord.setEnabled(true);
        btnStopRecord.setEnabled(false);
        btnPlay.setEnabled(true);
        btnStopPlay.setEnabled(false);


    }

    private void startPlaying() {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        isPlaying = true;
        btnRecord.setEnabled(false);
        btnStopRecord.setEnabled(false);
        btnPlay.setEnabled(false);
        btnStopPlay.setEnabled(true);
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        isPlaying = false;

        btnRecord.setEnabled(true);
        btnStopRecord.setEnabled(false);
        btnPlay.setEnabled(true);
        btnStopPlay.setEnabled(false);
    }

    private void setFileName() {
        String path = getExternalCacheDir().getAbsolutePath() + "/";
        String randomName = UUID.randomUUID().toString();
        mFileName = path + randomName + ".3gp";
    }

    public void showRunTimePermission() {

        if (isPermissionsGranted()) {
            // we already have the Permission, Now here we can do what we want ..!
            //
            // Toast.makeText(this, "Permission Already Granted!", Toast.LENGTH_SHORT).show();

        } else {
            // Permission is not Granted !
            // we should Request the Permission!

            // put all permissions you need in this Screen into string array
            String[] permissionsArray = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            //here we requet the permission
            ActivityCompat.requestPermissions(MainActivity.this, permissionsArray, STORAGE_PERMISSION_REQUEST);
        }

    }

    private boolean isPermissionsGranted() {

        boolean isWritingExternalStorageGranted = ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        boolean isAudioRecodingGrated = ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        return (isWritingExternalStorageGranted && isAudioRecodingGrated);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // user grants the Permission!
            // you can call the function to write/read to storage here!
            Toast.makeText(this, "Thank you for granting the Permission!", Toast.LENGTH_SHORT).show();

        } else {
            // user didn't grant the Permission we need
            Toast.makeText(this, "Please Grant the Permission To use this Feature!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activities, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (isPlaying) {
            stopPlaying();
        } else if (isRecording) {
            stopRecording();
        }

        switch (item.getItemId()) {
            case R.id.menu_main_activtiy: {
                Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.menu_google_code_activity: {
                startActivity(new Intent(getApplicationContext(), GoogleCodeActivity.class));
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
