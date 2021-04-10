package com.oh_72.prolit;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.oh_72.prolit.screens.MenuScreen;
import com.oh_72.prolit.screens.PlayScreen;
import com.oh_72.prolit.screens.StoreScreen;

public class Prolit extends Game {

	public final static String LOG_TAG = "PROLIT_LOGs";
	public final static String CLICK = "sounds/click.wav";

	public static final int MENU_STATE = 1;
	public static final int PLAY_STATE = 2;
	public static final int STORE_STATE = 3;

	public static int V_WIDTH = 1080;
	public static int V_HEIGHT = 1800;
	public final static float PPM = 100;
	public static int KNIFE_R = 60;
	public static int KNIFE_PAD = 5;
	public static int OBSTACLE_R = 30;

	public final static int KNIFE_BIT = 1;
	public final static int LOG_BIT = 2;
	public final static int OBSTACLE_BIT = 4;

	public static int MAX_CAM_SPEED = 20;
	public static int MAX_LOG_R = 200;
	public static int MIN_LOG_R = 50;

	private final static int TRANSITION = 30;
	private boolean goScreen;
	private final static float STEP = 1.0f / TRANSITION;
	private float currentStep;
	private boolean ending;

	public static float K = 1;

	public SpriteBatch batch;
	public PolygonSpriteBatch polyBatch;

	private int currentState = 1;
	public int nextState = 1;

	private int score;
	private int record;

	public static boolean sound;

	private int ball;
	private int balls;
	private int money;

	private Texture gameOver;

	public TextureRegion ballImgs[];
	public int count;

	private Preferences pref;

	private OrthographicCamera cam;
	private FitViewport viewport;

	public static AssetManager assetManager;

	private TextureAtlas atlas;

	@Override
	public void create () {
		batch = new SpriteBatch();
		polyBatch = new PolygonSpriteBatch();

		sound = true;
		pref = Gdx.app.getPreferences("Game");
		//pref.putInteger("bought", 0);
		pref.flush();
		record = pref.getInteger("record");
		ball = pref.getInteger("ball");
		balls = pref.getInteger("bought");
		money = pref.getInteger("money");

		atlas = new TextureAtlas("balls.pack");

		count = atlas.getRegions().size;
		ballImgs = new TextureRegion[count];
		int index = 0;
		for(TextureRegion region : atlas.getRegions()){
			ballImgs[index] = region;
			index++;
		}
//		ballImgs[1] = new Texture(Gdx.files.internal("tree.png"));
//		ballImgs[2] = new Texture(Gdx.files.internal("ship.png"));
//		ballImgs[3] = new Texture(Gdx.files.internal("knifes.png"));
//		ballImgs[4] = new Texture(Gdx.files.internal("eyes.png"));
//		ballImgs[5] = new Texture(Gdx.files.internal("cookie.png"));
//		ballImgs[6] = new Texture(Gdx.files.internal("among.png"));

		K = ((float) Gdx.app.getGraphics().getWidth()) / V_WIDTH;
		V_WIDTH =  Gdx.app.getGraphics().getWidth();
		V_HEIGHT =  Gdx.app.getGraphics().getHeight();

		gameOver = new Texture("transition.png");
		goScreen = false;
		currentStep = 0;
		ending = true;
		cam = new OrthographicCamera();
		viewport = new FitViewport(V_WIDTH, V_HEIGHT, cam);
		cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

		assetManager = new AssetManager();
		assetManager.load(CLICK, Sound.class);
		assetManager.load("sounds/death.wav", Sound.class);
		assetManager.load("sounds/jump.wav", Sound.class);
		assetManager.load("sounds/spin.wav", Sound.class);
		assetManager.load("sounds/music.wav", Music.class);
		assetManager.finishLoading();

//		Gdx.app.log(LOG_TAG, "Dimension: " + V_WIDTH + " :: " + V_HEIGHT + " :: " + K);

//		V_WIDTH *= K;
//		V_HEIGHT *= K;
		KNIFE_R *= K;
		OBSTACLE_R *= K;
		KNIFE_PAD *= K;
		MAX_CAM_SPEED *= K;
		MIN_LOG_R *= K;
		MAX_LOG_R *= K;

		newState(currentState);
	}

	private void newState(int i){
		if(currentState == 2){
			score = ((PlayScreen) this.getScreen()).getScore();
			money += score;
			pref.putInteger("money", money);
			pref.flush();
		}

		if(this.getScreen() != null) {
			this.getScreen().dispose();
		}

		switch (i){
			case 1:
				this.setScreen(new MenuScreen(this, score, record));
				break;
			case 2:
				this.setScreen(new PlayScreen(this, ballImgs[ball]));
				break;
			case 3:
				this.setScreen(new StoreScreen(this, money));
				break;
		}

		if(score >= record){
			record = score;
			pref.putInteger("record", record);
			pref.flush();
		}
	}

	public int getBalls(){
		return balls;
	}

	public int getBall(){
		return  ball;
	}

	public void selectBall(int ball){
		this.ball = ball;
		pref.putInteger("ball", ball);
		pref.flush();
	}

	public void buyBall(int ball){
		balls = ((1 << ball) | 1) | balls;
		pref.putInteger("bought", balls);
		pref.flush();
		Gdx.app.log(LOG_TAG, "Bought balls = " + balls);

		money -= 100;
		pref.putInteger("money", money);
		pref.flush();
	}

	@Override
	public void render () {

		if(!Prolit.sound) {
			assetManager.get("sounds/music.wav", Music.class).stop();
		}else{
			assetManager.get("sounds/music.wav", Music.class).play();
			assetManager.get("sounds/music.wav", Music.class).setVolume(0.1f);
		}

		Gdx.app.log(LOG_TAG, "TRA nextState = " + nextState);
		Gdx.app.log(LOG_TAG, "TRA currentState = " + currentState);
		Gdx.app.log(LOG_TAG, "TRA goScreen = " + goScreen);
		Gdx.app.log(LOG_TAG, "TRA currentStep = " + currentStep);
		Gdx.app.log(LOG_TAG, "TRA ending = " + ending);

		if(goScreen) {
			this.getScreen().show();
			goScreen = false;
			newState(nextState);
			currentState = nextState;
			Gdx.app.log(LOG_TAG, "#STATE = " + currentState);
		}
		batch.setColor(1, 1, 1, 1f);
		super.render();
		if(currentState != nextState){
			ending = false;

			currentStep += STEP;
			if(currentStep >= 1){
				currentStep = 1;
				goScreen = true;
				ending = true;
			}
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			batch.setColor(1, 1, 1, currentStep);
			batch.draw(gameOver, 0, 0 , V_WIDTH, V_HEIGHT);
			batch.end();
		}
		if(ending){
			currentStep -= STEP;
			if(currentStep <= 0){
				currentStep = 0;
				ending = false;
			}
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			batch.setColor(1, 1, 1, currentStep);
			batch.draw(gameOver, 0, 0, V_WIDTH, V_HEIGHT);
			batch.end();
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}

