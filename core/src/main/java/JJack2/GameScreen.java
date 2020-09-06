package JJack2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

//box2d imports
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.ContactImpulse;

//tilemap imports
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen extends BaseScreen {

    private Player player;
    private World world;
    private ArrayList<Box2DActor> removeList;
    private ParticleActor baseSparkle;
    TiledMap tiledMap;
    OrthographicCamera tiledCamera;
    TiledMapRenderer tiledMapRenderer;
    int[] backgroundLayer = {0};
    int[] tileLayer       = {1};
    //game world dimensions
    final int mapWidth = 1280; //bigger than before!
    final int mapHeight = 600;

    public GameScreen(BaseGame g) {
        super(g);
    }

    public void addSolid(RectangleMapObject rectangleMapObject){
        Rectangle rectangle = rectangleMapObject.getRectangle();
        Box2DActor solid = new Box2DActor();
        solid.setPosition(rectangle.x, rectangle.y);
        solid.setSize(rectangle.width, rectangle.height);
        solid.setStatic();
        solid.setShapeRectangle();
        solid.initializePhysics(world);
    }

    @Override
    public void create() {
        world = new World(new Vector2(0,-9.8f), true);
        removeList = new ArrayList<Box2DActor>();

        //background image provided by tilemap

        //player
        player = new Player();

        Animation walkAnimation = GameUtils.parseImageFiles("walk-", ".png", 3, 0.15f, PlayMode.LOOP_PINGPONG);
        player.storeAnimation("walk", walkAnimation);

        Texture standTexture = new Texture(Gdx.files.internal("stand.png"));
        standTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        player.storeAnimation("stand", standTexture);

        Texture jumpTexture = new Texture(Gdx.files.internal("jump.png"));
        jumpTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        player.storeAnimation("jump", jumpTexture);

        player.setSize(60, 90);
        mainStage.addActor(player);
        // set other player properties later

        // coin
        Coin baseCoin = new Coin();
        Texture coinTexture = new Texture(Gdx.files.internal("coin.png"));
        coinTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        baseCoin.storeAnimation("default", coinTexture);

        baseSparkle = new ParticleActor();
        baseSparkle.load("sparkle.pfx", "");

        // load tilemap
        tiledMap = new TmxMapLoader().load("platform_map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false, viewWidth, viewHeight);
        tiledCamera.update();

        MapObjects objects = tiledMap.getLayers().get("ObjectData").getObjects();
        for (MapObject object : objects){
            String name = object.getName();
            // all object data assumed to be stored as rectangles
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            Rectangle rectangle = rectangleMapObject.getRectangle();
            if (name.equals("player")){
                player.setPosition(rectangle.x, rectangle.y);
            }
            else if (name.equals("coin")){
                Coin coin = baseCoin.clone();
                coin.setPosition(rectangle.x, rectangle.y);
                mainStage.addActor(coin);
                coin.initializePhysics(world);
            }
            else
                System.err.println("Unknown tilemap object: " + name);
        }

        player.setDynamic();
        player.setShapeRectangle();
        player.setPhysicsProperties(1, 0.5f, 0.1f);
        player.setMaxSpeedX(2);
        player.setFixedRotation();
        player.initializePhysics(world);

        objects = tiledMap.getLayers().get("PhysicsData").getObjects();
        for (MapObject object : objects){
            if (object instanceof RectangleMapObject)
                addSolid((RectangleMapObject)object);
            else
                System.err.println("Unknown PhysicsData object.");
        }

        world.setContactListener(
                new ContactListener() {
                    @Override
                    public void beginContact(Contact contact) {
                        Object objC = GameUtils.getContactObject(contact, Coin.class);
                        if (objC != null){
                            Object objP = GameUtils.getContactObject(contact, Player.class, "main");
                            if (objP != null){
                                Coin coin = (Coin)objC;
                                removeList.add(coin);
                                ParticleActor sparkle = baseSparkle.clone();
                                sparkle.setPosition(coin.getX() + coin.getOriginX(),
                                        coin.getY() + coin.getOriginY());
                                sparkle.start();
                                mainStage.addActor(sparkle);
                            }
                            return; // avoid possible jumps
                        }

                        Object objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                        if (objP != null){
                            Player player = (Player) objP;
                            player.adjustGroundCount(1);
                            player.setActiveAnimation("stand");
                        }
                    }

                    @Override
                    public void endContact(Contact contact) {
                        Object objC = GameUtils.getContactObject(contact, Coin.class);
                        if (objC != null)
                            return;

                        Object objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                        if (objP != null){
                            Player player = (Player)objP;
                            player.adjustGroundCount(-1);
                        }
                    }

                    @Override
                    public void preSolve(Contact contact, Manifold oldManifold) {

                    }

                    @Override
                    public void postSolve(Contact contact, ContactImpulse impulse) {

                    }
                }
        );
    }

    @Override
    public void update(float dt) {
        removeList.clear();
        world.step(1/60f, 6,2);

        for (Box2DActor ba: removeList){
            ba.destroy();
            world.destroyBody(ba.getBody());
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT)){
            player.setScale(-1, 1);
            player.applyForce(new Vector2(-3.0f, 0));
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT)){
            player.setScale(1,1);
            player.applyForce(new Vector2(3.0f, 0));
        }

        if (player.getSpeed() > 0.1 && player.getAnimationName().equals("stand"))
            player.setActiveAnimation("walk");
        if (player.getSpeed() < 0.1 && player.getAnimationName().equals("walk"))
            player.setActiveAnimation("stand");
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.P)
            togglePaused();

        if (keycode == Keys.R)
            game.setScreen(new GameScreen(game));

        if (keycode == Keys.SPACE && player.isOnGround()){
            Vector2 jumpVector = new Vector2(0, 3);
            player.applyImpulse(jumpVector);
            player.setActiveAnimation("jump");
        }

        return false;
    }

    @Override
    public void render(float dt) {
        uiStage.act(dt);

        // only pause gameplay events, not UI events
        if ( !isPaused() )
        {
            mainStage.act(dt);
            update(dt);
        }

        // render
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Camera mainCamera = mainStage.getCamera();
        mainCamera.position.x = player.getX() + player.getOriginX();
        // bound main camera to layout
        mainCamera.position.x = MathUtils.clamp(mainCamera.position.x, viewWidth/2, mapWidth - viewWidth/2);
        mainCamera.update();

        // scroll background more slowly to create parallax effect
        tiledCamera.position.x = mainCamera.position.x / 4 + mapWidth / 4;
        tiledCamera.position.y = mainCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);
        tiledMapRenderer.render(backgroundLayer);

        tiledCamera.position.x = mainCamera.position.x;
        tiledCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);
        tiledMapRenderer.render(tileLayer);

        mainStage.draw();
        uiStage.draw();

    }
}
