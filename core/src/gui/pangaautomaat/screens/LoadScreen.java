package gui.pangaautomaat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import gui.pangaautomaat.MainClass;
import gui.pangaautomaat.NumberTextFieldFilter;

import java.util.Arrays;

public class LoadScreen implements Screen {
    private MainClass mainClass;
    private Stage stage;
    private TextField[][] textFields;
    public LoadScreen(MainClass mainClass){
        this.mainClass = mainClass;
    }
    @Override
    public void show() {
        stage = new Stage(mainClass.viewport,mainClass.batch);
        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);
        String[] valuutad =  MainClass.valuutad;
        Table table = new Table();
        table.setFillParent(true);
        int[] kupüürid = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(valuutad[0]));
        textFields = new TextField[valuutad.length][kupüürid.length];

        for (int i = 0; i < valuutad.length; i++) {
            table.add(new Label(valuutad[i],labelStyle));
            kupüürid = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(valuutad[i]));
            for (int j = 0; j < kupüürid.length; j++) {
                TextField kupüüridearvtextfield = new TextField(String.valueOf(kupüürid[j]), mainClass.skin);
                kupüüridearvtextfield.setTextFieldFilter(new NumberTextFieldFilter());
                textFields[i][j] = kupüüridearvtextfield;
                table.add(kupüüridearvtextfield).width(100).pad(5);
            }
            table.row();
        }


        final TextButton applybutton = new TextButton("Apply",mainClass.skin);
        applybutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                String[] valuutad =  MainClass.valuutad;
                for (int i = 0; i < valuutad.length; i++) {
                    int[] kupüürid = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(valuutad[i]));
                    String[] strings = new String[kupüürid.length];
                    for (int j = 0; j < kupüürid.length; j++) {
                        strings[j] = textFields[i][j].getText();
                    }
                    mainClass.prefs.preferences.putString(valuutad[i],mainClass.prefs.stringsToString(strings));
                }
                mainClass.prefs.save();
                mainClass.setScreen(new MainMenu(mainClass));
                dispose();
                return true;
            }
        });

        table.row();
        table.add(applybutton).colspan(10).padTop(20);
        stage.addActor(table);
        mainClass.inputMultiplexer.addProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

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
        mainClass.inputMultiplexer.removeProcessor(stage);
        stage.dispose();
    }
}
