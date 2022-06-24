package ru.gb.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MyGame extends ApplicationAdapter {
	private SpriteBatch batch;
//	private ShapeRenderer renderer;
	private Label label;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private OrthographicCamera camera;
	private List<Coin> coinList;
	private Texture fon;
	private Pers chip;

	private final int SPEED = 3;

	private int[] foreGround, backGround;

	private int score;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private boolean start =false;
	private Body heroBody;

	@Override
	public void create () {

		world = new World(new Vector2(0,-9.81f), true);
		debugRenderer = new Box2DDebugRenderer();

		BodyDef def = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape polygonShape = new PolygonShape();

		def.position.set(new Vector2(270f,250f));
		def.type = BodyDef.BodyType.StaticBody;
		fdef.density = 100f;
		fdef.friction = 17f;
//		fdef.restitution = 1f;

		polygonShape.setAsBox(100,10);
		fdef.shape = polygonShape;

		world.createBody(def).createFixture(fdef);

		def.position.set(new Vector2(130f,350f));

		polygonShape.setAsBox(100,10, new Vector2(0,0), 45 * MathUtils.radiansToDegrees);
//		polygonShape.setAsBox(100,10);
		fdef.shape = polygonShape;

		world.createBody(def).createFixture(fdef);

		def.type = BodyDef.BodyType.DynamicBody;
//		for (int i = 0; i < 10; i++) {
//			def.position.set(new Vector2(MathUtils.random(100f, 200f), 500f));
////		def.position.set(new Vector2(150f, 450f));
//			def.gravityScale = MathUtils.random(0.5f, 500f);
//			float size = MathUtils.random(3f, 15f);
//			polygonShape.setAsBox(size,size);
//			fdef.shape = polygonShape;
//			fdef.density = 100f;
//			fdef.friction = 0f;
//			world.createBody(def).createFixture(fdef);
////		Body body = world.createBody(def);
////		body.createFixture(fdef);
//		}

		def.position.set(new Vector2(270f,265));
		def.gravityScale = 4f;
		float size = 5;
		polygonShape.setAsBox(0f,0f);
		fdef.shape = polygonShape;
		fdef.density = 0f;
		fdef.friction = 1f;
		fdef.restitution = 0.2f;
		heroBody = world.createBody(def);
		heroBody.createFixture(fdef);


		polygonShape.dispose();

		chip = new Pers();
		fon = new Texture("fon.png");
		map = new TmxMapLoader().load("maps/level1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		foreGround = new int[1];
		foreGround[0] = map.getLayers().getIndex("Слой тайлов 2");
		backGround = new int[1];
		backGround[0] = map.getLayers().getIndex("Слой тайлов 1");

		batch = new SpriteBatch();
//		renderer = new ShapeRenderer();

		label = new Label(50);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Слой объектов 2").getObjects().get("camera");
		camera.position.x = o.getRectangle().x;
		camera.position.y = o.getRectangle().y;
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

	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);

		chip.setWalk(false);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			heroBody.applyForceToCenter(new Vector2(-300.0f,0.0f), true);
			camera.position.x-=SPEED;
			chip.setDir(true);
			chip.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			heroBody.applyForceToCenter(new Vector2(300.0f,0.0f), true);
			camera.position.x+=SPEED;
			chip.setDir(false);
			chip.setWalk(true);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			heroBody.applyForceToCenter(new Vector2(0.0f,4000.0f), true);
			camera.position.y+=SPEED;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//			heroBody.applyForceToCenter(new Vector2(0.0f,-3000.0f), true);
			camera.position.y-=SPEED;
		}
//		if (Gdx.input.isKeyPressed(Input.Keys.S)) start = true;

		camera.position.x = heroBody.getPosition().x;
		camera.position.y = heroBody.getPosition().y;
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
			Coin current = iterator.next();
			current.draw(batch, camera);
			if (current.isOverlaps(chip.getRect(), camera)) {
				iterator.remove();
				score++;
			}
		}
//		for (int i=0;i<coinList.size();i++){
//			coinList.get(i).draw(batch, camera);
//			if (coinList.get(i).isOverlaps(chip.getRect(), camera)) {
//				coinList.remove(i);
//				score++;
//			}
//		}

		batch.end();

//		renderer.begin(ShapeRenderer.ShapeType.Line);
//		for (int i=0;i<coinList.size();i++){
//			coinList.get(i).shapeDraw(renderer, camera);
//		}
//		renderer.end();

//		Color heroClr = new Color(Color.WHITE);
//		mapRenderer.render(foreGround);
//		renderer.setColor(heroClr);
//		renderer.begin(ShapeRenderer.ShapeType.Line);
//		for (int i=0;i<coinList.size();i++){
//			coinList.get(i).shapeDraw(renderer, camera);
//			if (coinList.get(i).isOverlaps(chip.getRect(), camera)) {
//				coinList.remove(i);
//				heroClr = Color.BLUE;
//			}
//		}
//		renderer.setColor(heroClr);
//		renderer.rect(heroRect.x, heroRect.y, heroRect.width, heroRect.height);
//		renderer.end();
//		if (start)
			world.step(1/60.0f,3,3);
		debugRenderer.render(world, camera.combined);
	}

	@Override
	public void dispose () {
		batch.dispose();
		coinList.get(0).dispose();
		world.dispose();
	}
}
