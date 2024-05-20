package com.danil.savecosmocanyon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;

public class MainActivity extends Activity {
    private Audio audio;
    private Music backgroundMusic;
    private boolean isBackgroundMusicPlaying;
    private boolean fromAnotherActivity;
    private ImageButton audioButton;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_activity_layout);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        // Sound
        fromAnotherActivity = false;
        audioButton = findViewById(R.id.audioButton);
        audio = new AndroidAudio(this);
        backgroundMusic = audio.newMusic("01-Prelude-FFVII OST.ogg");
        backgroundMusic.play();
        isBackgroundMusicPlaying = true;
        backgroundMusic.setLooping(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusic.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        backgroundMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fromAnotherActivity) {
            /*
            Bug in AndroidMusic.play(): when starting a new intent activity (HelpActivity) and then
            returning in this activity, the prepare() method in the
                if (!isPrepared)
                    mediaPlayer.prepare();
                mediaPlayer.start();
            code block caught an IOException() when requesting to play the music again.

            This was solved by calling dispose() to stop and release the background music and
            by assigning a new object to backgroundMusic like done in onCreate().
             */
            backgroundMusic.dispose();
            backgroundMusic = audio.newMusic("01-Prelude-FFVII OST.ogg");
            fromAnotherActivity = false;
        }

        if (isBackgroundMusicPlaying) {
            backgroundMusic.play();
        }

        // Highscore
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int score = preferences.getInt("HIGHSCORE", 0);
        TextView highscore = findViewById(R.id.highscore);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/in-game_font.ttf");
        highscore.setTypeface(tf);
        highscore.setText(String.format(getString(R.string.highscore), score));
    }

    public void startGame(View view) {
        fromAnotherActivity = true;
        Intent i = new Intent(getApplicationContext(), GameActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 0 || resultCode != Activity.RESULT_OK || data == null) {
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        int score = data.getIntExtra("Score", 0);
        if (score > preferences.getInt("HIGHSCORE", 0)) {
            editor.putInt("HIGHSCORE", score);
        }
        editor.apply();
    }

    public void exitGame(View view) {
        finish();
        System.exit(0);
    }

    public void manageAudio(View view) {
        if (backgroundMusic.isPlaying()) {
            audioButton.setBackgroundResource(R.drawable.audio_off);
            backgroundMusic.pause();
            isBackgroundMusicPlaying = false;
        } else {
            audioButton.setBackgroundResource(R.drawable.audio_on);
            backgroundMusic.play();
            isBackgroundMusicPlaying = true;
        }
    }

    public void popupHelp(View view) {
        fromAnotherActivity = true;
        Intent i = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(i);
    }
}
