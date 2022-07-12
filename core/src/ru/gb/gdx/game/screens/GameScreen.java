package ru.gb.gdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.gb.gdx.game.Coin;
import ru.gb.gdx.game.Label;
import ru.gb.gdx.game.Pers;
import ru.gb.gdx.game.PhysX;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private Label label;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private List<Coin> coinList;
    private Texture fon, pumpkin, hearts;
    private Button right, left, up;
    private Pers chip;
    private PhysX physX;
    private Music music;
    private int[] backGround;
    private int score;
    private int life = 3;
    private Sound sound;
    private boolean start = true;
    final Game game;
    ShaderProgram grayProgram, normalProgram, invertProgram;
    private float timeOfDay;
    float gs, persFriction, persRest;
    private int jumpStrength;

    public class Button {
        public TextureRegion textureRegion;
        public float x, y, width, height;

        public Button(final TextureRegion textureRegion) {
            this.textureRegion = textureRegion;
        }
    }

    public GameScreen(final Game game) {
        this.game = game;
        chip = new Pers();
        fon = new Texture("fon.png");
        map = new TmxMapLoader().load("maps/level1.tmx");
        hearts = new Texture("heart3.png");

        right = new Button(new TextureRegion());
        left = new Button(new TextureRegion());
        up = new Button(new TextureRegion());

        right.textureRegion.setTexture(new Texture("right.png"));
        left.textureRegion.setTexture(new Texture("left.png"));
        up.textureRegion.setTexture(new Texture("up.png"));

        right.width = right.textureRegion.getTexture().getWidth();
        right.height = right.textureRegion.getTexture().getHeight();
        left.width = left.textureRegion.getTexture().getWidth();
        left.height = left.textureRegion.getTexture().getHeight();
        up.width = up.textureRegion.getTexture().getWidth();
        up.height = up.textureRegion.getTexture().getHeight();

        up.x = Gdx.graphics.getWidth()-right.width-up.width-5.0f;
        up.y = hearts.getHeight() + left.height + 5.0f;
        right.x = Gdx.graphics.getWidth()-right.width-5.0f;
        right.y = hearts.getHeight() + 5.0f;
        left.x = Gdx.graphics.getWidth()-right.width-up.width-left.width-5.0f;
        left.y = hearts.getHeight() + 5.0f;

        mapRenderer = new OrthogonalTiledMapRenderer(map);


        physX = new PhysX();
        if (map.getLayers().get("land") != null){
            MapObjects mo = map.getLayers().get("land").getObjects();
            physX.addObjects(mo);
            MapObject pers = map.getLayers().get("land").getObjects().get("camera");
            physX.addObject(pers);
            physX.enemyInit();
            gs = physX.getPers().getGravityScale();
        }

        sound = Gdx.audio.newSound(Gdx.files.internal("259172__xtrgamr__uhoh.wav"));
        backGround = new int[2];
        backGround[0] = map.getLayers().getIndex("Слой тайлов 2");
        backGround[1] = map.getLayers().getIndex("Слой тайлов 1");

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        label = new Label(50);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        RectangleMapObject o = (RectangleMapObject) map.getLayers().get("land").getObjects().get("camera");

        camera.position.x = physX.getPers().getPosition().x;
        camera.position.y = physX.getPers().getPosition().y;
        camera.zoom = 0.5f;
        camera.update();

        coinList = new ArrayList<>();
        MapLayer ml = map.getLayers().get("coins");
        if (ml != null){
            MapObjects mo = ml.getObjects();
            if (mo.getCount()>0){
                for (int i=0;i<mo.getCount();i++){
                    RectangleMapObject tmpMo = (RectangleMapObject) ml.getObjects().get(i);
                    Rectangle rect = tmpMo.getRectangle();
                    coinList.add(new Coin(new Vector2(rect.x,rect.y)));
                }
            }
        }

        music = Gdx.audio.newMusic(Gdx.files.internal("Jets, The - Chip 'n Dale Rescue Rangers (Чип и Дейл) (minus 4).mp3"));
        music.setLooping(true);
        music.setVolume(0.025f);
        music.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(final float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (physX.cl.isOnGround()) {
            jumpStrength = 0;
        }

        chip.setWalk(false);
        if (physX.cl.isOnGround() || physX.cl.isClimb()) {
            chip.setJump(false);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && ((Gdx.input.getX() > left.x && Gdx.input.getX() < (left.x+left.width)) && ((Gdx.input.getY()-Gdx.graphics.getHeight()/2-left.y-5.0f) > left.y && (Gdx.input.getY()-Gdx.graphics.getHeight()/2-left.y-5.0f) < (left.y+left.height))))) {
                if (physX.cl.isOnGround()) {
                    physX.setPersForce(new Vector2(physX.getRunForce() * -1, 0.0f));
                    chip.setDir(true);
                    chip.setWalk(true);
                    chip.setJump(false);
                } else {
                    physX.setPersForce(new Vector2(physX.getFlyForce() * -1, 0.0f));
                }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && ((Gdx.input.getX() > right.x && Gdx.input.getX() < (right.x+right.width)) && ((Gdx.input.getY()-Gdx.graphics.getHeight()/2-right.y-5.0f) > right.y && (Gdx.input.getY()-Gdx.graphics.getHeight()/2-right.y-5.0f) < (right.y+right.height))))) {
                if (physX.cl.isOnGround()) {
                    physX.setPersForce(new Vector2(physX.getRunForce(), 0.0f));
                    chip.setDir(false);
                    chip.setWalk(true);
                } else {
                    physX.setPersForce(new Vector2(physX.getFlyForce(), 0.0f));
                }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (physX.cl.isOnGround() && (!physX.cl.isClimb())) {
                    physX.setPersForce(new Vector2(0.0f, physX.getJumpForce()));
                    chip.setJump(true);
                } else if (physX.cl.isClimb()) {
                    physX.setPersForce(new Vector2(0.0f, physX.getClimbForce()));
                    chip.setJump(false);
                }
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || (Gdx.input.isTouched() && ((Gdx.input.getX() > up.x && Gdx.input.getX() < (up.x+up.width)) && ((Gdx.input.getY()-Gdx.graphics.getHeight()/2+up.height-5.0f) > up.y && (Gdx.input.getY()-Gdx.graphics.getHeight()/2+up.height-5.0f) < (up.y+up.height))))) {
                    if (physX.cl.isOnGround() && (!physX.cl.isClimb())) {
                        physX.setPersForce(new Vector2(0.0f, 300.0f));
                        chip.setJump(true);
                    } else if (physX.cl.isClimb()) {
                        physX.setPersForce(new Vector2(0.0f, physX.getClimbForce()));
                        chip.setJump(false);
                    }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            start = true;
            physX.getPers().setGravityScale(gs);
            physX.setPersForce(new Vector2(0.0f, 1500.0f));
            physX.setPersForce(new Vector2(1500.0f, 0.0f));
        }

        if (physX.cl.isClimb()){
            start = false;
            physX.getPers().setGravityScale(0.0f);
        } else {
            start = true;
            physX.getPers().setGravityScale(gs);
        }

        camera.position.x = physX.getPers().getPosition().x;
        camera.position.y = physX.getPers().getPosition().y;
        camera.update();

        batch.begin();
        batch.draw(fon, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render(backGround);

        batch.begin();
        batch.draw(chip.getFrame(), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        label.draw(batch, "Монеток собрано: "+String.valueOf(score), 0, 0);

        if (life!=physX.cl.getLife()) {
            sound.play(0.025f,1,0);
            life = physX.cl.getLife();
        }
        switch (physX.cl.getLife()) {
            case 1:
                hearts = new Texture("heart1.png");
                break;
            case 2:
                hearts = new Texture("heart2.png");
                break;
            case 3:
                hearts = new Texture("heart3.png");
                break;
        }

        batch.draw(hearts,Gdx.graphics.getWidth()-hearts.getWidth(),0, hearts.getWidth(), hearts.getHeight());
        batch.draw(right.textureRegion.getTexture(), right.x,right.y, right.width, right.height);
//        batch.draw(down.textureRegion.getTexture(), down.x,down.y, down.width, down.height);
        batch.draw(left.textureRegion.getTexture(), left.x,left.y, left.width, left.height);
        batch.draw(up.textureRegion.getTexture(), up.x,up.y, up.width, up.height);

        ListIterator<Coin> iterator = coinList.listIterator();
        while (iterator.hasNext()) {
            int state;

            Coin current = iterator.next();
            state = current.draw(batch, camera);
            if (current.isOverlaps(chip.getRect(), camera)) {
                if (state==0) current.setState();
                if (state==1) {
                    iterator.remove();
                    score++;
                    if (score > 16) {
                        music.stop();
                        game.setScreen(new WinScreen(game));
                    }
                }
            }
        }

        batch.end();

        physX.step();
//        physX.debugDraw(camera);

        if (physX.cl.getLife() < 1 || physX.cl.isFatal()) {
            dispose();
            game.setScreen(new GameOverScreen(game));
        }

        pumpkin = new Texture("Pumpkin.png");

        batch.begin();
        for (Fixture fixture: physX.enemyBodys) {
            float cx = ((fixture.getBody().getPosition().x - camera.position.x)/camera.zoom + Gdx.graphics.getWidth()/2)-pumpkin.getWidth()/2;
            float cy = ((fixture.getBody().getPosition().y - camera.position.y)/camera.zoom + Gdx.graphics.getHeight()/2)-pumpkin.getHeight()/2;
            batch.draw(pumpkin, cx,cy);
        }
        batch.end();
    }

    @Override
    public void resize(final int width, final int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        coinList.get(0).dispose();
        physX.dispose();
        music.stop();
        music.dispose();
        sound.dispose();
    }
}
