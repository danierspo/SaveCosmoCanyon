package com.danil.savecosmocanyon;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIATBGaugeDrawableComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperSuspensionPhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperWheelPhysicsBodyComponent;
import com.google.fpl.liquidfun.DistanceJointDef;

import java.nio.ByteOrder;

public class GameActivity extends Activity {
    public Audio audio;
    public Music backgroundMusic;
    private boolean isBackgroundMusicPlaying;

    private AndroidFastRenderView renderView;

    private static boolean isPaused = false;
    private static final int GAME_TIME = 150;

    // Boundaries of the physical simulation
    public static final float XMIN = -24, XMAX = 24, YMIN = -13.5f, YMAX = 13.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        // Sound
        audio = new AndroidAudio(this);
        CollisionSounds.init(audio);
        backgroundMusic = audio.newMusic("85-Ending Credits-FFVII OST.ogg");
        backgroundMusic.play();
        backgroundMusic.setVolume(0.7f);
        isBackgroundMusicPlaying = true;
        backgroundMusic.setLooping(true);

        isPaused = false;

        // Game world
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX),
                screenSize   = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        GameWorld gw = new GameWorld(physicalSize, screenSize, this);

        // UI: NextMeteor
        gw.addGameObject(GameWorld.makeUINextMeteor(gw, 0f, -12.5f));

        // UI: ATBGauge
        gw.addGameObject(GameWorld.makeUIATBGauge(gw, -12.5f, -12.5f));

        // Enclosure
        gw.addGameObject(GameWorld.makeEnclosure(gw, XMIN-1, XMAX+1, YMIN-1, YMAX-3f));

        // Left scraper and its suspensions and wheels
        GameObject leftScraper = GameWorld.makeScraper(gw, -20f, 7f);
        gw.addGameObject(leftScraper);
        GameObject rearSuspension = GameWorld.makeScraperSuspension(gw, -20f - 1.384f, 7f + 0.50f, leftScraper);
        gw.addGameObject(rearSuspension);
        GameObject frontSuspension = GameWorld.makeScraperSuspension(gw, -20f + 1.008f, 7f + 0.50f, leftScraper);
        gw.addGameObject(frontSuspension);
        GameObject rearWheel = GameWorld.makeScraperWheel(gw, -20f - 1.384f, 7f + 0.50f + 0.75f, rearSuspension, leftScraper); // fix position or what
        gw.addGameObject(rearWheel);
        GameObject frontWheel = GameWorld.makeScraperWheel(gw, -20f + 1.008f, 7f + 0.50f + 0.75f, frontSuspension, leftScraper); // fix position or what
        gw.addGameObject(frontWheel);

        // UI: Left scraper HPBar
        gw.addGameObject(GameWorld.makeUIScraperHPBar(gw, leftScraper));

        // DistanceJoint between left scraper's rear and front wheels
        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.setBodyA(((ScraperWheelPhysicsBodyComponent) rearWheel.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef.setBodyB(((ScraperWheelPhysicsBodyComponent) frontWheel.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef.setLocalAnchorA(0,0);
        distanceJointDef.setLocalAnchorB(0,0);
        distanceJointDef.setLength(1.384f + 1.008f);
        distanceJointDef.setCollideConnected(false);
        gw.getWorld().createJoint(distanceJointDef);

        // DistanceJoint between left scraper's rear and front suspensions
        distanceJointDef.setBodyA(((ScraperSuspensionPhysicsBodyComponent) rearSuspension.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef.setBodyB(((ScraperSuspensionPhysicsBodyComponent) frontSuspension.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef.setLocalAnchorA(0,0);
        distanceJointDef.setLocalAnchorB(0,0);
        distanceJointDef.setLength(1.384f + 1.008f);
        distanceJointDef.setCollideConnected(false);
        gw.getWorld().createJoint(distanceJointDef);
        distanceJointDef.delete();

        // Right scraper and its suspensions and wheels
        GameObject rightScraper = GameWorld.makeScraper(gw, 20f, 7f);
        gw.addGameObject(rightScraper);
        GameObject rearSuspension2 = GameWorld.makeScraperSuspension(gw, 20f - 1.008f, 7f + 0.50f, rightScraper);
        gw.addGameObject(rearSuspension2);
        GameObject frontSuspension2 = GameWorld.makeScraperSuspension(gw, 20f + 1.384f, 7f + 0.50f, rightScraper);
        gw.addGameObject(frontSuspension2);
        GameObject rearWheel2 = GameWorld.makeScraperWheel(gw, 20f - 1.008f, 7f + 0.50f + 0.75f, rearSuspension2, rightScraper);
        gw.addGameObject(rearWheel2);
        GameObject frontWheel2 = GameWorld.makeScraperWheel(gw, 20f + 1.384f, 7f + 0.50f + 0.75f, frontSuspension2, rightScraper);
        gw.addGameObject(frontWheel2);

        // UI: Right scraper HPBar
        gw.addGameObject(GameWorld.makeUIScraperHPBar(gw, rightScraper));

        // DistanceJoint between right scraper's rear and front wheels
        DistanceJointDef distanceJointDef2 = new DistanceJointDef();
        distanceJointDef2.setBodyA(((ScraperWheelPhysicsBodyComponent) frontWheel2.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef2.setBodyB(((ScraperWheelPhysicsBodyComponent) rearWheel2.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef2.setLocalAnchorA(0,0);
        distanceJointDef2.setLocalAnchorB(0,0);
        distanceJointDef2.setLength(1.384f + 1.008f);
        distanceJointDef2.setCollideConnected(false);
        gw.getWorld().createJoint(distanceJointDef2);

        // DistanceJoint between right scraper's rear and front suspensions
        distanceJointDef2.setBodyA(((ScraperSuspensionPhysicsBodyComponent) rearSuspension2.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef2.setBodyB(((ScraperSuspensionPhysicsBodyComponent) frontSuspension2.getComponent(ComponentType.PHYSICS_BODY)).body);
        distanceJointDef2.setLocalAnchorA(0,0);
        distanceJointDef2.setLocalAnchorB(0,0);
        distanceJointDef2.setLength(1.384f + 1.008f);
        distanceJointDef2.setCollideConnected(false);
        gw.getWorld().createJoint(distanceJointDef2);
        distanceJointDef2.delete();

        // UI: PauseButton
        gw.addGameObject(GameWorld.makeUIPauseButton(gw, 23f, -12.5f));

        // UI: AudioButton
        gw.addGameObject(GameWorld.makeUIAudioButton(gw, 23f, -10.5f));

        // UI: TimeLeft
        gw.addGameObject(GameWorld.makeUITimeLeft(gw, -23.5f, -12.5f, GAME_TIME));

        // UI: Score
        gw.addGameObject(GameWorld.makeUIScore(gw, 10f, -12.5f));

        // UI: PauseWindow
        gw.addGameObject(GameWorld.makeUIPauseWindow(gw));

        // UI: GameOverWindow
        gw.addGameObject(GameWorld.makeUIGameOverWindow(gw));

        // UI: StartingCountdown
        gw.addGameObject(GameWorld.makeUIStartingCountdown(gw));

        // Just for info
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRate = display.getRefreshRate();
        Log.i(getString(R.string.app_name), "Refresh rate =" + refreshRate);

        // Accelerometer
        SensorManager smanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (smanager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty()) {
            Log.i(getString(R.string.app_name), "No accelerometer");
        } else {
            Sensor accelerometer = smanager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            if (!smanager.registerListener(new AccelerometerListener(gw), accelerometer, SensorManager.SENSOR_DELAY_NORMAL))
                Log.i(getString(R.string.app_name), "Could not register accelerometer listener");
        }

        // View
        renderView = new AndroidFastRenderView(this, gw);
        setContentView(renderView);

        // Touch
        MultiTouchHandler touch = new MultiTouchHandler(renderView, 1, 1);
        // Setter needed due to cyclic dependency
        gw.setTouchHandler(touch);

        Log.i(getString(R.string.app_name), "onCreate complete, Endianness = " +
                (ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN? "Big Endian" : "Little Endian"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("Game thread", "pause");
        renderView.pause(); // stops the main loop
        renderView.gameworld.AI_thread.pause();
        backgroundMusic.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Game thread", "stop");
        backgroundMusic.pause();

        /*
        A game can be paused, but can't be suspended. That is because AndroidFastRenderView's
        deltaTime is reset when the app is stopped; consequently, managing the ATB gauge when
        stopping an activity needs more reasoning.
         */

        UIATBGaugeDrawableComponent.resetATBGauge();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("Game thread", "resume");
        renderView.resume(); // Starts game loop in a separate thread
        renderView.gameworld.AI_thread.resume();

        if (isBackgroundMusicPlaying) {
            backgroundMusic.play();
        }
    }

    public void manageAudio() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
            isBackgroundMusicPlaying = false;
        } else {
            backgroundMusic.play();
            isBackgroundMusicPlaying = true;
        }
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public AndroidFastRenderView getRenderView() {
        return renderView;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;

        if (paused) {
            renderView.gameworld.AI_thread.pause();
        } else {
            renderView.gameworld.AI_thread.resume();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setMusic(String filename) {
        backgroundMusic.stop();
        backgroundMusic = audio.newMusic(filename);
        if (isBackgroundMusicPlaying) {
            backgroundMusic.play();
            backgroundMusic.setVolume(0.7f);
            backgroundMusic.setLooping(false);
        }
    }

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap BACK again to return to the main menu.", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }
}
