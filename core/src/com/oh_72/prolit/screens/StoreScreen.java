package com.oh_72.prolit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.oh_72.prolit.Prolit;

public class StoreScreen implements Screen, Disposable {

    private Prolit game;
    private OrthographicCamera gameCam;
    private FitViewport gamePort;

    private TextureAtlas atlas;

    private Texture background;
    private TextureRegion arrow_up;
    private TextureRegion arrow_down;
    private TextureRegion item_up;
    private TextureRegion item_down;

    private Stage stage;

    private Image btnBack;
    private Label lblMoney;

    private Image btns[];
    private Image images[];
    private Label labels[];
    private TextureRegion balls[];

    private int count;

    private int money;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont moneyFont;
    private BitmapFont itemFont;

    private GlyphLayout glyphLayout;

    public StoreScreen(Prolit game, int money){
        this.game = game;
        this.money = money;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Prolit.V_WIDTH / Prolit.PPM, Prolit.V_HEIGHT / Prolit.PPM, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        atlas = new TextureAtlas("shop.pack");

        background = new Texture("background.png");
        arrow_up = atlas.findRegion("arrow_up");
        arrow_down = atlas.findRegion("arrow_down");
        item_up = atlas.findRegion("shop_item_up");
        item_down = atlas.findRegion("shop_item_down");

        glyphLayout = new GlyphLayout();

        balls = game.ballImgs;
        count = balls.length;

        initStage();
    }

    private void initStage(){
        btns = new Image[count];
        images = new Image[count];
        labels = new Label[count];

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (100 * Prolit.K);
        parameter.borderWidth = 5 * Prolit.K;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.GOLD;
        moneyFont = generator.generateFont(parameter);

        parameter.size = (int) (50 * Prolit.K);
        parameter.borderWidth = 5 * Prolit.K;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        itemFont = generator.generateFont(parameter);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        float cell = Prolit.V_WIDTH / 11;
        float padding = 10 * Prolit.K;
        String pathSkin = "skin/skin.json";

        btnBack = new Image(new TextureRegionDrawable(arrow_up));
        btnBack.setSize(cell * 3, cell * 2);
        //btnBack.getStyle().imageUp = new TextureRegionDrawable(back);
        btnBack.setPosition(padding, Prolit.V_HEIGHT - padding - btnBack.getHeight());
        btnBack.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnBack.setDrawable(new TextureRegionDrawable(arrow_up));
                if(x >= 0 && x <= btnBack.getWidth()) {
                    if (y >= 0 && y <= btnBack.getHeight()) {
                        game.nextState = Prolit.MENU_STATE;
                    }
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(Prolit.sound) {
                    Prolit.assetManager.get(Prolit.CLICK, Sound.class).play();
                }
                btnBack.setDrawable(new TextureRegionDrawable(arrow_down));
                return true;
            }
        });
        stage.addActor(btnBack);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = moneyFont;
        style.fontColor = Color.WHITE;

        lblMoney = new Label(String.format("%d", money), style);
        lblMoney.setPosition(Prolit.V_WIDTH - lblMoney.getWidth() - padding,
                Prolit.V_HEIGHT - padding - lblMoney.getHeight());
        stage.addActor(lblMoney);

        float startHeight = Prolit.V_HEIGHT - padding - btnBack.getHeight() - cell;

        float btnWidth = cell * 3.5f;
        float btnHeight = cell * 4.4f;
        float btnOffset = (Prolit.V_WIDTH - btnWidth * 3) / 4;

        float imgWidth = cell * 2.2f;
        float imgHeight = cell * 2.2f;
        float imgOffset = (btnWidth - imgWidth) / 2;

        Label.LabelStyle itemStyle = new Label.LabelStyle();
        itemStyle.font = itemFont;

        for(int i = 0; i < Math.ceil(count / 3.0); i++){
            for(int ii = 0; ii < 3; ii++){
                final int index = i * 3 + ii;
                if(index == count){
                    break;
                }

                final boolean bought = ((game.getBalls() >> index) & 1) == 1;

                InputListener inputListener = new InputListener(){
                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        btns[index].setDrawable(new TextureRegionDrawable(item_up));
                        if(x >= 0 && x <= btns[index].getWidth()) {
                            if (y >= 0 && y <= btns[index].getHeight()) {
                                if(!(((game.getBalls() >> index) & 1) == 1)) {
                                    if(money >= 100) {
                                        game.buyBall(index);
                                        money -= 100;
                                    }
                                }else{
                                    game.selectBall(index);
                                }
                                update();
                            }
                        }
                    }

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        //Gdx.app.log(Prolit.LOG_TAG, "to buy");
                        if(Prolit.sound) {
                            Prolit.assetManager.get(Prolit.CLICK, Sound.class).play();
                        }
                        btns[index].setDrawable(new TextureRegionDrawable(item_down));
                        return true;
                    }
                };

                btns[index] = new Image(new TextureRegionDrawable(item_up));
                btns[index].setSize(btnWidth, btnHeight);
                btns[index].setPosition(btnOffset * (ii + 1) + btnWidth * ii, startHeight - (btnHeight + btnOffset) * (i + 1));
                btns[index].addListener(inputListener);
                stage.addActor(btns[index]);

                images[index] = new Image(balls[index]);
                images[index].setSize(imgWidth, imgHeight);
                //images[index].setBounds(0, 0, imgWidth, imgHeight);
                images[index].addListener(inputListener);
                images[index].setPosition(btns[index].getX() + imgOffset, btns[index].getY() + (btnHeight - imgHeight - imgOffset * 1.3f));
                stage.addActor(images[index]);

                labels[index] = new Label("100", itemStyle);
                labels[index].setColor(Color.GOLD);
                labels[index].addListener(inputListener);
                if(bought){
                    //labels[index].getStyle().font.setColor(Color.WHITE);
                    labels[index].setColor(Color.WHITE);
                    labels[index].setText("bought");
                }
                if(index == game.getBall()){
                    labels[index].setColor(Color.WHITE);
                    //labels[index].getStyle().font.setColor(Color.WHITE);
                    labels[index].setText("equipped");
                }
                //labels[index].setSize(imgWidth, imgHeight);
                glyphLayout.setText(itemFont, labels[index].getText());
                labels[index].setPosition(btns[index].getX() + btns[index].getWidth() / 2 - glyphLayout.width / 2
                        , btns[index].getY() + 70 * Prolit.K);
                stage.addActor(labels[index]);
            }
        }

    }

    public void update(){
        lblMoney.setText(String.format("%d", money));

        for(int i = 0; i < Math.ceil(count / 3.0); i++){
            for(int ii = 0; ii < 3; ii++){
                final int index = i * 3 + ii;
                if(index == count){
                    break;
                }

                final boolean bought = ((game.getBalls() >> index) & 1) == 1;

                if(bought){
                    labels[index].setColor(Color.WHITE);
                    //labels[index].getStyle().font.setColor(Color.WHITE);
                    labels[index].setText("bought");
                    glyphLayout.setText(itemFont, labels[index].getText());
                    labels[index].setPosition(btns[index].getX() + btns[index].getWidth() / 2 - glyphLayout.width / 2
                            , btns[index].getY() + 70 * Prolit.K);
                }
                if(index == game.getBall()){
                    labels[index].setColor(Color.WHITE);
                    //labels[index].getStyle().font.setColor(Color.WHITE);
                    labels[index].setText("equipped");
                    glyphLayout.setText(itemFont, labels[index].getText());
                    labels[index].setPosition(btns[index].getX() + btns[index].getWidth() / 2 - glyphLayout.width / 2
                            , btns[index].getY() + 70 * Prolit.K);
                }
            }
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        stage.dispose();
    }
}
