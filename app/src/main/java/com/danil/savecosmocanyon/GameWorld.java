package com.danil.savecosmocanyon;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.GameObjectsPool;
import com.danil.savecosmocanyon.entity_component.ais.ScraperAIComponent;
import com.danil.savecosmocanyon.entity_component.alives.ScraperAliveComponent;
import com.danil.savecosmocanyon.entity_component.collidables.EnclosureCollidableComponent;
import com.danil.savecosmocanyon.entity_component.collidables.MeteorCollidableComponent;
import com.danil.savecosmocanyon.entity_component.collidables.ScraperCollidableComponent;
import com.danil.savecosmocanyon.entity_component.collidables.ScraperSuspensionCollidableComponent;
import com.danil.savecosmocanyon.entity_component.collidables.ScraperWheelCollidableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.EnclosureDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.MeteorDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ScraperDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ScraperSuspensionDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ScraperWheelDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIAudioButtonDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UINextMeteorDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIPauseButtonDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIScoreDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIScraperHPBarDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIStartingCountdownDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UITimeLeftDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIATBGaugeDrawableComponent;
import com.danil.savecosmocanyon.entity_component.grounds.EnclosureGroundComponent;
import com.danil.savecosmocanyon.entity_component.physics.EnclosurePhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.physics.MeteorPhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperPhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperSuspensionPhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperWheelPhysicsBodyComponent;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;

import java.util.Collection;

import com.danil.savecosmocanyon.entity_component.GameObject;

/**
 * The game objects and the viewport.
 *
 * Created by mfaella on 27/02/16.
 */
public class GameWorld {
    final static int bufferWidth = 1920, bufferHeight = 1080;
    Bitmap buffer;
    private final Canvas canvas;

    // Simulation
    private static final int POOL_MAX_SIZE = 20;
    public GameObjectsPool gameObjectsPool;
    SparseArray<GameObject> objects;
    SparseArray<GameObject> objectsDeletionList;
    protected volatile SparseArray<GameObject> leftMeteorsList;
    protected volatile SparseArray<GameObject> rightMeteorsList;
    public SparseArray<GameObject> scrapersList;
    public SparseArray<GameObject> scraperWheelsList;
    public static GameObject leftScraperHPBar = null;
    public static GameObject rightScraperHPBar = null;
    public static GameObject pauseWindow = null;
    public static GameObject gameoverWindow = null;
    public static GameObject timeLeft = null;
    World world;
    final Box physicalSize, screenSize, currentView;
    private final MyContactListener contactListener;
    private final TouchConsumer touchConsumer;
    private TouchHandler touchHandler;

    // Particles
    ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;

    // Parameters for world simulation
    // private static final float TIME_STEP = 1 / 50f; // 50 fps
    private static final int VELOCITY_ITERATIONS = 7; // Suggested: 8
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    final Activity activity; // just for loading bitmaps in game objects
    private final Bitmap bitmap;
    private final Rect dstRect;

    public final AIThread AI_thread = new AIThread(this);

    // Arguments are in physical simulation units.
    // physicalSize: boundaries of the physical simulation
    // screenSize: size of the screen
    public GameWorld(Box physicalSize, Box screenSize, Activity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 0);  // gravity vector

        this.currentView = physicalSize;
        // Start with half the world
        // new Box(physicalSize.xmin, physicalSize.ymin, physicalSize.xmax, physicalSize.ymin + physicalSize.height/2);

        // The particle system
        ParticleSystemDef psysdef = new ParticleSystemDef();
        this.particleSystem = world.createParticleSystem(psysdef);
        particleSystem.setRadius(PARTICLE_RADIUS);
        particleSystem.setMaxParticleCount(MAXPARTICLECOUNT);
        psysdef.delete();

        // Stored to prevent GC

        contactListener = new MyContactListener();
        world.setContactListener(contactListener);

        touchConsumer = new TouchConsumer(this);

        this.gameObjectsPool = new GameObjectsPool(GameObject::new, POOL_MAX_SIZE);

        this.objects = new SparseArray<>();
        this.objectsDeletionList = new SparseArray<>();
        this.leftMeteorsList = new SparseArray<>();
        this.rightMeteorsList = new SparseArray<>();
        this.scrapersList = new SparseArray<>();
        this.scraperWheelsList = new SparseArray<>();
        this.canvas = new Canvas(buffer);

        dstRect = new Rect();
        canvas.getClipBounds(dstRect);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(
                this.activity.getResources(), R.drawable.basiccanyon_background, o
        );
        canvas.drawBitmap(bitmap, null, dstRect, null);
    }

    public synchronized GameObject addGameObject(GameObject obj) {
        objects.put(obj.hashCode(), obj);
        return obj;
    }

    public synchronized void removeGameObject(GameObject obj) {
        if (obj.getComponent(ComponentType.PHYSICS_BODY) != null) {
            objectsDeletionList.put(obj.hashCode(), obj);
        }
        objects.remove(obj.hashCode());
        this.gameObjectsPool.free(obj);
    }

    public synchronized void addParticleGroup(GameObject obj) {
        objects.put(obj.hashCode(), obj);
    }

    public synchronized void update(float elapsedTime) {
        if (!((GameActivity) activity).isPaused()) { // Used to pause the simulation if Pause button was clicked while playing
            world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

            // Handle collisions
            handleCollisions(contactListener.getCollisions());

            /* IMPORTANT!
            Delete objects if they fall in the canyon. Previously, this was done in render(),
            which supposedly (not always) made the game crash: in fact, this must be done after
            world.step(), because an object must not be deleted with world.destroyBody while
            world.step is executing, because of the collision listener. This is why I decided to
            implement objectsDeletionList to add objects to a list and remove them after world.step
            is executed (as suggested by the official documentation). However, it could still happen
            that deletion takes more time than world.step execution, and the game loop actually
            gets locked.

            Source (StackOverflow):
            https://gamedev.stackexchange.com/questions/136967/libgdx-crash-when-destroying-a-body

            Source (iforce2d):
            https://www.iforce2d.net/b2dtut/removing-bodies

            Also, check the "Implicit Destruction" section in the official documentation:
            https://box2d.org/documentation/md__d_1__git_hub_box2d_docs_loose_ends.html

            DestructionListener not implemented in JLiquidFun:
            https://box2d.org/documentation/classb2_destruction_listener.html
            */
            if (objectsDeletionList.size() > 0) {
                for (int i = 0; i < objectsDeletionList.size(); i++) {
                    world.destroyBody(objectsDeletionList.valueAt(i).body);
                }
                objectsDeletionList.clear();
            }
        }

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents())
            touchConsumer.consumeTouchEvent(event);
    }

    public synchronized void render() {
        canvas.drawBitmap(bitmap, null, dstRect, null);

        for (int i=0; i < objects.size(); i++) {
            objects.valueAt(i).draw(buffer);
        }
    }

    private void handleCollisions(Collection<Collision> collisions) {
        /* IMPORTANT!
        Collision sounds have been managed in Collidable components' doAtCollision() method:
        when a contact occurs between two objects, the specific sound is played. This approach has
        been chosen instead of the CollisionSounds.getSound(Class<?> a, Class<?> b) approach
        because MeteorCollidableComponent can play different sounds, one for each meteor fragment
        (Normal, Magic, Summon) when they hit a scraper plus one when a fragment hits the ground,
        due to the Entity-Component architecture.
        Moreover, creating three different subclasses seemed to add more complexity than actually
        needed: CollisionSounds maintains one variable for each Sound (which uses a SoundPool) and
        no HashMap is used, implying less overhead. In fact, sound distancing is not needed.

        However, a CollisionsPool in MyContactListener has been implemented and this method is used
        to free the pool after the contact occurred, granting a recycle instead of constantly
        creating a new Collision object.

        In the following for, "Do something" suggests that anything regarding the collision between
        the two objects can still be done, while the sound is managed elsewhere as mentioned above.
        The CollisionsPool just recycles previously created Collision objects (most of the time
        not the same objects that actually collided), but real collisions are still stored in
        MyContactListener.cache, which the Collection<Collision> argument of this method is
        retrieved from.
         */

        for (Collision event: collisions) {
            // Do something

            contactListener.collisionsPool.free(event);
        }
    }

    // Conversions between screen coordinates and physical coordinates
    public float toMetersX(float x) {
        return currentView.xmin + x * (currentView.width/screenSize.width);
    }
    public float toMetersY(float y) {
        return currentView.ymin + y * (currentView.height/screenSize.height);
    }

    public float toPixelsX(float x) {
        return (x-currentView.xmin)/currentView.width*bufferWidth;
    }
    public float toPixelsY(float y) {
        return (y-currentView.ymin)/currentView.height*bufferHeight;
    }

    public float toPixelsXLength(float x)
    {
        return x/currentView.width*bufferWidth;
    }
    public float toPixelsYLength(float y)
    {
        return y/currentView.height*bufferHeight;
    }

    public synchronized void setGravity(float x, float y)
    {
        world.setGravity(x, y);
    }

    @Override
    public void finalize()
    {
        world.delete();
    }

    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }

    public TouchConsumer getTouchConsumer() {
        return touchConsumer;
    }

    public World getWorld() {
        return this.world;
    }

    public Bitmap getBuffer() {
        return this.buffer;
    }

    public ParticleSystem getParticleSystem() {
        return this.particleSystem;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public Box getCurrentView() {
        return this.currentView;
    }

    public SparseArray<GameObject> getLeftMeteorsList() {
        return leftMeteorsList;
    }

    public SparseArray<GameObject> getRightMeteorsList() {
        return rightMeteorsList;
    }

    public SparseArray<GameObject> getScraperWheelsList() {
        return scraperWheelsList;
    }

    public SparseArray<GameObject> getScrapersList() {
        return scrapersList;
    }



    // Entity-Component methods for objects creation follow

    public synchronized static GameObject makeEnclosure(GameWorld gw, float xmin, float xmax, float ymin, float ymax) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new EnclosurePhysicsBodyComponent(gw, xmin, xmax, ymin, ymax));
        go.addComponent(new EnclosureDrawableComponent(gw, xmin, xmax, ymin, ymax));
        go.addComponent(new EnclosureCollidableComponent());
        go.addComponent(new EnclosureGroundComponent());

        return go;
    }

    public synchronized static GameObject makeScraper(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new ScraperAliveComponent(100));
        // go.addComponent(new ScraperTouchableComponent(go));
        go.addComponent(new ScraperPhysicsBodyComponent(gw, x, y));
        go.addComponent(new ScraperDrawableComponent(gw, x));
        go.addComponent(new ScraperCollidableComponent(x));
        go.addComponent(new ScraperAIComponent(gw.activity.getApplicationContext(), "scraper.json", gw));

        gw.scrapersList.put(go.hashCode(), go);

        return go;
    }

    public synchronized static GameObject makeScraperSuspension(GameWorld gw, float x, float y, GameObject scraper) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new ScraperSuspensionPhysicsBodyComponent(gw, x, y, scraper));
        go.addComponent(new ScraperSuspensionDrawableComponent(gw, scraper));
        go.addComponent(new ScraperSuspensionCollidableComponent(scraper));

        return go;
    }

    public synchronized static GameObject makeScraperWheel(GameWorld gw, float x, float y, GameObject suspension, GameObject scraper) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new ScraperWheelPhysicsBodyComponent(gw, x, y, suspension, 0));
        go.addComponent(new ScraperWheelDrawableComponent(gw,scraper));
        go.addComponent(new ScraperWheelCollidableComponent(suspension));

        gw.scraperWheelsList.put(go.hashCode(), go);

        return go;
    }

    public synchronized static GameObject makeMeteor(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new MeteorPhysicsBodyComponent(gw, x, y));
        go.addComponent(new MeteorDrawableComponent(gw,
                ((MeteorPhysicsBodyComponent) go.getComponent(ComponentType.PHYSICS_BODY)).meteorType));
        go.addComponent(new MeteorCollidableComponent());

        UINextMeteorDrawableComponent.generateNextMeteor();

        return go;
    }

    public synchronized static GameObject makeUIPauseButton(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UIPauseButtonDrawableComponent(gw, x, y));

        return go;
    }

    public synchronized static GameObject makeUIAudioButton(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UIAudioButtonDrawableComponent(gw, x, y));

        return go;
    }

    public synchronized static GameObject makeUITimeLeft(GameWorld gw, float x, float y, int gameTime) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UITimeLeftDrawableComponent(gw, x, y, gameTime));
        timeLeft = go;

        return go;
    }

    public synchronized static GameObject makeUINextMeteor(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UINextMeteorDrawableComponent(gw, x, y));

        return go;
    }

    public synchronized static GameObject makeUIATBGauge(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UIATBGaugeDrawableComponent(gw, x, y));

        return go;
    }

    public synchronized static GameObject makeUIScraperHPBar(GameWorld gw, GameObject scraper) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UIScraperHPBarDrawableComponent(gw, scraper));
        if (scraper.body.getPositionX() <= 0) {
            leftScraperHPBar = go;
        } else {
            rightScraperHPBar = go;
        }

        return go;
    }

    public synchronized static GameObject makeUIScore(GameWorld gw, float x, float y) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UIScoreDrawableComponent(gw, x, y));

        return go;
    }

    public synchronized static GameObject makeUIPauseWindow(GameWorld gw) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        // go.addComponent(new UIPauseWindowDrawableComponent(gw));
        pauseWindow = go;

        return go;
    }

    public synchronized static GameObject makeUIGameOverWindow(GameWorld gw) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        // go.addComponent(new UIGameOverWindowDrawableComponent(gw));
        gameoverWindow = go;

        return go;
    }

    public synchronized static GameObject makeUIStartingCountdown(GameWorld gw) {
        GameObject go = gw.gameObjectsPool.newObject(gw);
        go.addComponent(new UIStartingCountdownDrawableComponent(gw));

        return go;
    }
}
