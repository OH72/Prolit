package com.oh_72.prolit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.oh_72.prolit.Prolit;

public class MenuScreen implements Screen {

    private Prolit game;

    private OrthographicCamera gameCam;
    private FitViewport gamePort;

    private Texture background;

    private Texture mute_up;
    private Texture unmute_up;
    private Texture mute_down;
    private Texture unmute_down;
    private Texture shop_up;
    private Texture shop_down;

    private Label btnStart;
    private Image btnUnmute;
    private Image btnShop;

    private Label score;
    private Label record;
    private Label scoreText;
    private Label recordText;

    private TextButton.TextButtonStyle style;
    private Skin skin;

    private Stage stage;

    private InputProcessor input;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameters;
    private BitmapFont buttonFont;
    private BitmapFont labelFont;

    public MenuScreen(final Prolit game, int score, int record){
        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Prolit.V_WIDTH / Prolit.PPM, Prolit.V_HEIGHT / Prolit.PPM, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2,
                gamePort.getWorldHeight() / 2, 0);
        //gameCam.position.set(Prolit.V_WIDTH / 2, Prolit.V_HEIGHT / 2, 0);
        //gameCam.position.set(0, 0, 0);
        Gdx.app.log(Prolit.LOG_TAG, "world dimension MENU = "
                + gameCam.position.x + " :: " + gameCam.position.y);


        initStage(score, record);
    }

    private void initStage(int score, int record){
        background = new Texture("background.png");
        mute_up = new Texture("mute_up.png");
        unmute_up = new Texture("unmute_up.png");
        mute_down = new Texture("mute_down.png");
        unmute_down = new Texture("unmute_down.png");
        shop_up = new Texture("shop_up.png");
        shop_down = new Texture("shop_down.png");

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        String pathSkin = "skin/sgx-ui.json";

        float cellW = 100 * Prolit.K;
        float cellH = 100 * Prolit.K;
        float padding = 50 * Prolit.K;


        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.size = (int) (200 * Prolit.K);
        parameters.borderColor = Color.BLACK;
        parameters.borderWidth = 5 * Prolit.K;
        parameters.color = Color.WHITE;
        buttonFont = generator.generateFont(parameters);

        parameters.size = (int) (120 * Prolit.K);
        labelFont = generator.generateFont(parameters);


        btnShop = new Image(new TextureRegionDrawable(new TextureRegion(shop_up)));
        btnShop.setSize(cellW * 2, cellH * 1.5f);
        btnShop.setWidth(cellW * 2.6f);
        btnShop.setHeight(btnShop.getWidth() * shop_up.getHeight() / shop_up.getWidth());
        btnShop.setPosition(padding, padding);
        btnShop.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                btnShop.setDrawable(new TextureRegionDrawable(new TextureRegion(shop_up)));
                if(x >= 0 && x <= btnShop.getWidth()){
                    if(y >= 0 && y <= btnShop.getHeight()){
                        game.nextState = Prolit.STORE_STATE;
                    }
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(Prolit.sound) {
                    Prolit.assetManager.get(Prolit.CLICK, Sound.class).play();
                }
                btnShop.setDrawable(new TextureRegionDrawable(new TextureRegion(shop_down)));
                return true;
            }
        });
        stage.addActor(btnShop);

        btnUnmute = new Image(new TextureRegionDrawable(new TextureRegion(unmute_up)));
        btnUnmute.setSize(cellW * 2.5f, cellH * 2);
        btnUnmute.setPosition(Prolit.V_WIDTH - btnUnmute.getWidth() - cellW / 3, cellH / 3);
        btnUnmute.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(Prolit.LOG_TAG, "START button x & y: " + btnUnmute.getX() + " :: " + btnUnmute.getY());
                Gdx.app.log(Prolit.LOG_TAG, "START button width & height: " + btnUnmute.getWidth() + " :: " + btnUnmute.getHeight());
                Gdx.app.log(Prolit.LOG_TAG, "START button point: " + x + " :: " + y);
                if(x >= 0 && x <= btnUnmute.getWidth()){
                    if(y >= 0 && y <= btnUnmute.getHeight()){
                        game.sound = !game.sound;
                        if(game.sound){
                            btnUnmute.setDrawable(new TextureRegionDrawable(new TextureRegion(unmute_up)));
                        }else{
                            btnUnmute.setDrawable(new TextureRegionDrawable(new TextureRegion(mute_up)));
                        }
                    }
                }else{
                    if(game.sound){
                        btnUnmute.setDrawable(new TextureRegionDrawable(new TextureRegion(unmute_up)));
                    }else{
                        btnUnmute.setDrawable(new TextureRegionDrawable(new TextureRegion(mute_up)));
                    }
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(Prolit.sound) {
                    Prolit.assetManager.get(Prolit.CLICK, Sound.class).play();
                }
                if(game.sound){
                    btnUnmute.setDrawable(new TextureRegionDrawable(new TextureRegion(unmute_down)));
                }else{
                    btnUnmute.setDrawable(new TextureRegionDrawable(new TextureRegion(mute_down)));
                }
                return true;
            }
        });
        stage.addActor(btnUnmute);

        //Skin skin = new Skin(Gdx.files.internal(pathSkin));
        //skin.get(TextButton.TextButtonStyle.class).font = buttonFont;
        //skin.add("default-font", customFont, BitmapFont.class);
        btnStart = new Label("PLAY", new Label.LabelStyle(buttonFont, Color.WHITE));
        //btnStart.getStyle().font.setColor(Color.WHITE);
        //btnStart.getStyle().font = FreeTypeFontFactory.createBitmapFont(Gdx.files.internal("font.ttf"), FONT_CHARACTERS, 12.5f, 7.5f, 1.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //btnStart.getStyle().font.getData().setScale(2 * Prolit.K);
        //btnStart.getStyle().downFontColor = Color.LIGHT_GRAY;

        //btnStart.setSize(cellW * 2f, cellH * 1f);
        btnStart.setPosition((Prolit.V_WIDTH - btnStart.getWidth()) / 2, (Prolit.V_HEIGHT - btnStart.getHeight()) / 2);
        btnStart.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                btnStart.getStyle().fontColor = Color.WHITE;
                if(x >= 0 && x <= btnStart.getWidth()){
                    if(y >= 0 && y <= btnStart.getHeight()){
                        game.nextState = Prolit.PLAY_STATE;
                    }
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(Prolit.sound) {
                    Prolit.assetManager.get(Prolit.CLICK, Sound.class).play();
                }
                btnStart.getStyle().fontColor = Color.LIGHT_GRAY;
                return true;
            }
        });
        stage.addActor(btnStart);

        Label.LabelStyle style = new Label.LabelStyle();
//        BitmapFont font = new BitmapFont();
//        font.getData().setScale(7 * Prolit.K);
        style.font = labelFont;
        style.fontColor = Color.WHITE;

        this.record = new Label(String.format("%5d", record), style);
        recordText = new Label("RECORD:", style);

        Table labelTable = new Table();
        labelTable.add(recordText).expandX();
        labelTable.add(this.record).expandX();

        if(score > 0){
            this.score = new Label(String.format("%5d", score), style);
            scoreText = new Label("SCORE:", style);

            labelTable.row();
            labelTable.add(scoreText).expandX();
            labelTable.add(this.score).expandX();
        }

        labelTable.setFillParent(true);
        labelTable.top();

        stage.addActor(labelTable);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //gameCam.update();
        //Gdx.app.log(Prolit.LOG_TAG, "world dimension MENU = "
         //       + gameCam.position.x + " :: " + gameCam.position.y);
        Gdx.gl.glClearColor(0, 0.3f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0,
                Prolit.V_WIDTH / Prolit.PPM, Prolit.V_HEIGHT / Prolit.PPM);
        game.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
        background.dispose();
        stage.dispose();
        shop_up.dispose();
        shop_down.dispose();
        unmute_up.dispose();
        mute_up.dispose();
        unmute_down.dispose();
        mute_down.dispose();
        buttonFont.dispose();
        labelFont.dispose();
    }
}
