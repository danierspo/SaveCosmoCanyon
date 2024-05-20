package com.danil.savecosmocanyon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class AndroidFastRenderView extends SurfaceView implements Runnable {
    private final Bitmap framebuffer;
    private Thread renderThread = null;
    private final SurfaceHolder holder;
    public GameWorld gameworld;
    private volatile boolean running = false;

    private float fpsDeltaTime, startTime, currentTime;
    
    public AndroidFastRenderView(Context context, GameWorld gw) {
        super(context);
        this.gameworld = gw;
        this.framebuffer = gw.buffer;
        this.holder = getHolder();
    }

    public float getFpsDeltaTime() {
        return this.fpsDeltaTime;
    }
    public float getStartTime() {
        return this.startTime;
    }
    public float getCurrentTime() {
        return this.currentTime;
    }

    /** Starts the game loop in a separate thread.
     */
    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();         
    }

    /** Stops the game loop and waits for it to finish
     */
    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // Just retry
            }
        }
    }

    /** Runs the Game Main Loop
     */
    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime(), fpsTime = startTime, frameCounter = 0;
        this.startTime = startTime;

        while (running) {
            if (!holder.getSurface().isValid()) {
                // Too soon (busy waiting): this only happens on startup and resume
                continue;
            }

            long currentTime = System.nanoTime();
            this.currentTime = currentTime;

            // deltaTime is in seconds
            float deltaTime = (currentTime-startTime) / 1000000000f,
                  fpsDeltaTime = (currentTime-fpsTime) / 1000000000f;
            startTime = currentTime;

            this.fpsDeltaTime = fpsDeltaTime;

            gameworld.update(deltaTime);
            gameworld.render();

            // Draws framebuffer on screen
            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);

            // Scales to actual screen resolution
            canvas.drawBitmap(framebuffer, null, dstRect, null);

            holder.unlockCanvasAndPost(canvas);

            // Measures FPS
            frameCounter++;
            if (fpsDeltaTime > 1) { // Prints every second
                Log.d("FastRenderView", "Current FPS = " + frameCounter);
                frameCounter = 0;
                fpsTime = currentTime;
            }
        }
    }
}
