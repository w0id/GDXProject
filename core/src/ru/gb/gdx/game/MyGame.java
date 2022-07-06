package ru.gb.gdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MyGame extends ApplicationAdapter {
	public static SpriteBatch batch;
//	private ShapeRenderer renderer;
	private Label label;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private OrthographicCamera camera;
	private List<Coin> coinList;
	private Texture fon;
	private Pers chip;
	private PhysX physX;
	float gs,persFriction,persRest;
	private boolean start = false;
	private Music music;

	private final int SPEED = 3;

	private int[] foreGround, backGround;

	private int score;

	@Override
	public void create () {

		chip = new Pers();
		fon = new Texture("fon.png");
		map = new TmxMapLoader().load("maps/level1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);


		physX = new PhysX();
		if (map.getLayers().get("land") != null){
			MapObjects mo = map.getLayers().get("land").getObjects();
			physX.addObjects(mo);
			MapObject pers = map.getLayers().get("land").getObjects().get("camera");
			physX.addObject(pers);
			gs = physX.getPers().getGravityScale();
			persFriction = physX.getPers().getFixtureList().get(0).getFriction();
			persRest = physX.getPers().getFixtureList().get(0).getRestitution();
		}

		backGround = new int[2];
		backGround[0] = map.getLayers().getIndex("Слой тайлов 2");
		backGround[1] = map.getLayers().getIndex("Слой тайлов 1");

		batch = new SpriteBatch();

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

		music = Gdx.audio.newMusic(Gdx.files.internal("Soundtracks — Чип и Дейл (Disney, 1989) (www.lightaudio.ru).mp3"));
		music.setLooping(true);
		music.setVolume(0.025f);
		music.play();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);

		chip.setWalk(false);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (physX.cl.isOnGround()) {
				physX.setPersForce(new Vector2(-600.0f, 0.0f));
				chip.setDir(true);
				chip.setWalk(true);
			} else {
				physX.setPersForce(new Vector2(-60.0f, 0.0f));
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (physX.cl.isOnGround()) {
				physX.setPersForce(new Vector2(600.0f, 0.0f));
				chip.setDir(false);
				chip.setWalk(true);
			} else {
				physX.setPersForce(new Vector2(60.0f, 0.0f));
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			if (physX.cl.isOnGround() && (!physX.cl.isClimb()))
				physX.setPersForce(new Vector2(0.0f,600.0f));
			else if (physX.cl.isClimb())
				physX.setPersForce(new Vector2(0.0f, 20.0f));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
			start = true;
			physX.getPers().setGravityScale(gs);
			physX.getPers().getFixtureList().get(0).setFriction(persFriction);
			physX.getPers().getFixtureList().get(0).setRestitution(persRest);
			physX.setPersForce(new Vector2(0.0f, 1500.0f));
			physX.setPersForce(new Vector2(1500.0f, 0.0f));
		}

		if (physX.cl.isClimb()){
			start = false;
			physX.getPers().setGravityScale(0.0f);
			physX.getPers().getFixtureList().get(0).setFriction(100.0f);
			physX.getPers().getFixtureList().get(0).setRestitution(0.0f);
		} else {
			start = true;
			physX.getPers().setGravityScale(gs);
			physX.getPers().getFixtureList().get(0).setFriction(persFriction);
			physX.getPers().getFixtureList().get(0).setRestitution(persRest);
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
		batch.draw(chip.getFrame(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		label.draw(batch, "Монеток собрано: "+String.valueOf(score), 0, 0);

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
				}
			}
		}

		batch.end();

			physX.step();
		physX.debugDraw(camera);
	}

	@Override
	public void dispose () {
		batch.dispose();
		coinList.get(0).dispose();
		physX.dispose();
		music.stop();
		music.dispose();
	}

}
