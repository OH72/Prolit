package com.oh_72.prolit.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oh_72.prolit.Prolit;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont labelFont;

    private Label score;
    private Label textScore;

    public Hud(SpriteBatch sb){

        viewport = new FitViewport(Prolit.V_WIDTH, Prolit.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (100 * Prolit.K);
        parameter.borderWidth = 5 * Prolit.K;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        labelFont = generator.generateFont(parameter);


        Table table = new Table();
        table.bottom();
        table.setFillParent(true);

        score = new Label(String.format("%05d", 0), new Label.LabelStyle(labelFont, Color.WHITE));
        textScore = new Label("SCORE:", new Label.LabelStyle(labelFont, Color.WHITE));

        table.add(textScore).expandX().padTop(10);
        table.add(score).expandX().padTop(10);

        stage.addActor(table);
    }

    public void setPoint(int height){
        score.setText(String.format("%05d", height));
    }


    public void dispose(){
        labelFont.dispose();
        stage.dispose();
    }


}
