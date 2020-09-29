package com.balert.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTS extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech mTts;
    private String spokenText;


    public TTS() {
    }

        @Override
        public void onCreate () {

        mTts = new TextToSpeech(this, this);

        // This is a good place to set spokenText
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        while(true){
            if(mTts!=null){
                String msg = intent.getExtras().getString("msg");
                mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                break;
            }
        }
        return START_STICKY;
    }

    @Override
        public void onInit ( int status){
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

        @Override
        public void onUtteranceCompleted (String uttId){
        stopSelf();
    }

        @Override
        public void onDestroy () {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

        @Override
        public IBinder onBind (Intent arg0){
        return null;
    }

    }
