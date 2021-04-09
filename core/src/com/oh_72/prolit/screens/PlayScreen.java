package com.oh_72.prolit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.oh_72.prolit.Prolit;
import com.oh_72.prolit.scenes.Hud;
import com.oh_72.prolit.sprites.Ball;
import com.oh_72.prolit.sprites.Logs;
import com.oh_72.prolit.tools.WorldContactListener;


public class PlayScreen implements Screen {

    private Prolit game;
    private Hud hud;

    private OrthographicCamera gameCam;
    private FitViewport gamePort;

    private World world;
    private Box2DDebugRenderer b2dr;

    private InputProcessor input;

    private Logs logs;
    private Ball ball;

    private Texture background;

    private int score;

    private boolean boost;

    private int firstCamY;

    private float camSpeed;
    private float stopDist;

    public PlayScreen(final Prolit game, Texture knifeSprite) {

        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Prolit.V_WIDTH / Prolit.PPM, Prolit.V_HEIGHT / Prolit.PPM, gameCam);
        //gamePort = new ScreenViewport();
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        firstCamY = (int)gameCam.position.y;

        background = new Texture("background.png");

        hud = new Hud(game.batch);

        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new WorldContactListener());
        b2dr = new Box2DDebugRenderer();

        logs = new Logs(this);
//        logs.add(new Log(this, 100, 100, 100, -3));
//        logs.add(new Log(this, 100, 800, 100, 2));
//        logs.add(new Log(this, 100, 100, 900, -10));
//        logs.add(new Log(this, 100, 800, 600, 5));
//
//        obstacles = new ArrayList<Obstacle>();
//        obstacles.add(new Obstacle(this, logs.get(1), 50));
//        obstacles.add(new Obstacle(this, logs.get(2), 100));

        this.ball = new Ball(this, logs.getLogs().get(0), knifeSprite);

        boost = false;
        camSpeed = 0;

        input = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if(screenX < Prolit.V_WIDTH / 2) {
                    boost = true;
                    ball.getLog().setBoost(true);
                }else {
                    ball.throwKnife();
                }
                //hud.setPoint(screenX, screenY);
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if(boost){
                    boost = false;
                    ball.getLog().setBoost(false);
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        };
    }

    public World getWorld(){
        return world;
    }

    public void handleInput(float delta){
        Gdx.input.setInputProcessor(input);
    }


    public void update(float delta){
        handleInput(delta);
        if(!ball.isToMenu()) {
            world.step(1 / 60f, 6, 2);
            logs.update(delta);
            ball.update(delta);
            if (ball.isOnLog()) {
                logs.madeNewLogs(gameCam.position.y);
            }

            gameCamSpeed();

            score = (int)gameCam.position.y - firstCamY;
            hud.setPoint(score);
            gameCam.update();
        }else {
            game.nextState = Prolit.MENU_STATE;
            //this.dispose();
        }
    }

    private void gameCamSpeed(){
        if (ball.getMax() > gameCam.position.y) {
            stopDist = (0 + camSpeed) / 2 * camSpeed;
            if(ball.getMax() - gameCam.position.y <= stopDist / Prolit.PPM){
                camSpeed--;
                Gdx.app.log(Prolit.LOG_TAG, "#bug stopDist = " + stopDist + " ( " + camSpeed + " )");

            }else{
                camSpeed += 1;
                if(camSpeed > Prolit.MAX_CAM_SPEED){
                    camSpeed = Prolit.MAX_CAM_SPEED;
                }
                Gdx.app.log(Prolit.LOG_TAG, "#bug stopDist = " + stopDist + " ( " + camSpeed + " )");
            }
            gameCam.position.y += camSpeed / Prolit.PPM;
        }else {
            camSpeed = 0;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.5f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //renderer.render();

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        game.batch.draw(background, 0, gameCam.position.y - Prolit.V_HEIGHT / 2 / Prolit.PPM,
                Prolit.V_WIDTH / Prolit.PPM, Prolit.V_HEIGHT / Prolit.PPM);

        game.batch.end();

        game.polyBatch.setProjectionMatrix(gameCam.combined);
        game.polyBatch.begin();
        logs.drawObstacle(game.polyBatch);
        game.polyBatch.end();

        //logs.drawObstacle(game.batch);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        ball.draw(game.batch);
        logs.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        game.batch.setProjectionMatrix(gameCam.combined);
        //b2dr.render(world, gameCam.combined);
    }

    public OrthographicCamera getGameCam(){
        return gameCam;
    }

    public int getScore(){
        return score;
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
        logs.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
