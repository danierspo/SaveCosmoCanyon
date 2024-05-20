package com.danil.savecosmocanyon;

import android.graphics.BitmapFactory;

import com.badlogic.androidgames.framework.Input;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.TouchableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIAudioButtonDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIPauseButtonDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIATBGaugeDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIPauseWindowDrawableComponent;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.QueryCallback;

// No dragging with MouseJoint planned
/**
 * Takes care of user interaction.
 */
public class TouchConsumer {
    private Fixture touchedFixture;

    private final GameWorld gw;
    private final QueryCallback touchQueryCallback = new TouchQueryCallback();
    public boolean gameOver;
    public boolean starting;

    private class TouchQueryCallback extends QueryCallback {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }

    /**
        scale{X,Y} are the scale factors from pixels to physics simulation coordinates
    */
    public TouchConsumer(GameWorld gw) {
        this.gw = gw;
        gameOver = false;
        starting = true;
    }

    public void consumeTouchEvent(Input.TouchEvent event) {
        if (event.type == Input.TouchEvent.TOUCH_DOWN) {
            consumeTouchDown(event);
        }
    }

    private void consumeTouchDown(Input.TouchEvent event) {
        if (!gameOver && !starting) {
            float x = gw.toMetersX(event.x), y = gw.toMetersY(event.y);
            float x_pixels = gw.toPixelsX(x), y_pixels = gw.toPixelsY(y);

            GameActivity activity = (GameActivity) gw.getActivity();

            // If it's the audio button. It has to be clickable even if the game is paused
            if (UIAudioButtonDrawableComponent.dest.contains(x_pixels, y_pixels)) {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inScaled = false;
                if (activity.getBackgroundMusic().isPlaying()) {
                    UIAudioButtonDrawableComponent.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.audio_off, o);
                } else {
                    UIAudioButtonDrawableComponent.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.audio_on, o);
                }

                activity.manageAudio();
                return;
            }

            if (activity.isPaused()) { // If the activity is paused
                activity.setPaused(false);
                UIATBGaugeDrawableComponent.pausedTimeStop += System.nanoTime();
                GameWorld.pauseWindow.removeComponent(ComponentType.DRAWABLE);
            } else {
                // If it's the pause button
                if (UIPauseButtonDrawableComponent.dest.contains(x_pixels, y_pixels)) {

                    activity.setPaused(!activity.isPaused());
                    UIATBGaugeDrawableComponent.pausedTimeStart += System.nanoTime();
                    UIATBGaugeDrawableComponent.needToRecomputePausedTime = true;

                    UIPauseWindowDrawableComponent uiPauseWindowDrawableComponent =
                            new UIPauseWindowDrawableComponent(gw);
                    GameWorld.pauseWindow.addComponent(uiPauseWindowDrawableComponent);

                    return;
                }

                // Game view clicked
                //Log.d("MultiTouchHandler", "touch down at " + x + ", " + y);

                touchedFixture = null;
                gw.world.queryAABB(touchQueryCallback, x, y, x, y);
                //Log.d("touchedFixture", "" + touchedFixture + ", x = " + x + ", y = " + y);
                if (touchedFixture != null) {
                    // From fixture to GameObject
                    Body touchedBody = touchedFixture.getBody();
                    Object userData = touchedBody.getUserData();
                    if (userData != null) {
                        PhysicsBodyComponent touchedObject = (PhysicsBodyComponent) userData;
                        TouchableComponent touchedObjectComponent = (TouchableComponent) touchedObject.getOwner().getComponent(ComponentType.TOUCHABLE);
                        if (touchedObjectComponent != null) {
                            touchedObjectComponent.doAtTouch();
                            return;
                        }
                    }
                }

                if (UIATBGaugeDrawableComponent.getYourTurn() && UIATBGaugeDrawableComponent.delay == 0f) {
                    UIATBGaugeDrawableComponent.setYourTurn(false);
                    gw.addGameObject(GameWorld.makeMeteor(gw, x, -16));
                }
            }
        }
    }
}
