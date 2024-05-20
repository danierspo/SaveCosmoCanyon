package com.danil.savecosmocanyon;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;

public class HelpActivity extends Activity {
    private Music backgroundMusic;
    private boolean isBackgroundMusicPlaying;
    private ImageButton audioButton;

    private final String[] hints = {
            "Right when Meteor is about to collide with the planet Gaia, the Holy magic is " +
                    "summoned and, with the help of the Lifestream, the huge meteor is destroyed; " +
                    "but its fragments are approaching slowly the planet...",

            "Gaia's destruction is avoided, and some scrapers are already set to clean the " +
                    "ground from Meteor's fragments falling in Cosmo Canyon...",

            "Will the scrapers accomplish their mission?",

            "Make a Meteor fragment fall on the ground in the position you choose, and have fun " +
                    "watching the scrapers trying to pull it in the central canyon... Or vice versa. :)",

            "Now, let's take a closer look at the interface.",

            "On the top-left, you can check how much time remains for the current game.",

            "The 'Go!' bar, called ATB Gauge, tells you when you are able to launch a fragment: when it's " +
                    "green, just go! Otherwise, it will show you a red 'Wait' bar and you will have to wait for it" +
                    " to fill.",

            "In the top-center of the screen, you can see which is the next fragment that will be thrown: " +
                    "the grey rock-like fragment will damage the scrapers if it falls on their body, but " +
                    "it's not the only one. In fact, two more fragment types can be generated, so... Find " +
                    "out the different game mechanics!",

            "On its right, you are able to check the actual score of the game. IMPORTANT: if and only " +
                    "if the scrapers are damaged, you will get +25 score. However, if you manage to make " +
                    "the scrapers fall in the canyon, there is a surprise for you. Try your best to make it " +
                    "happen ASAP!",

            "This was the last hint. To leave the Help section, click the top-right Home button. Actually, " +
                    "in game you will be able to: take a break by clicking the Pause button (that takes the " +
                    "place of the Home button), pause the music by clicking the Music button (well, you can do " +
                    "it here too, but music is just too good to be muted, don't you think?).",

            "For more specific hints and technical details, please take a look at the game guide. Enjoy!"
    };
    private int index;

    private TextView hintsContainer;
    private ImageButton previousButton;
    private ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.help_activity_layout);

        index = 0;
        hintsContainer = findViewById(R.id.hintsContainer);
        hintsContainer.setMovementMethod(new ScrollingMovementMethod());
        hintsContainer.setText(hints[0]);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        // Sound
        audioButton = findViewById(R.id.helpAudioButton);
        Audio audio = new AndroidAudio(this);
        backgroundMusic = audio.newMusic("45-Cosmo Canyon-FFVII OST.ogg");
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
        if (isBackgroundMusicPlaying) {
            backgroundMusic.play();
        }
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

    public void home(View view) {
        finish();
    }

    public void nextHint(View view) {
        hintsContainer.setText(hints[++index]);
        if (index == (hints.length) - 1) {
            nextButton.setVisibility(View.INVISIBLE);
            nextButton.setClickable(false);
        } else {
            if (previousButton.getVisibility() == View.INVISIBLE) {
                previousButton.setVisibility(View.VISIBLE);
                previousButton.setClickable(true);
            }
        }
    }

    public void previousHint(View view) {
        hintsContainer.setText(hints[--index]);
        if (index == 0) {
            previousButton.setVisibility(View.INVISIBLE);
            previousButton.setClickable(false);
        } else {
            if (nextButton.getVisibility() == View.INVISIBLE) {
                nextButton.setVisibility(View.VISIBLE);
                nextButton.setClickable(true);
            }
        }
    }
}
