package gui.pangaautomaat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import gui.pangaautomaat.MainClass;

public class WaitingScreen implements Screen {
    private MainClass mainClass;
    private Stage stage;
    public WaitingScreen(MainClass mainClass){
        this.mainClass = mainClass;
    }
    @Override
    public void show() {
        stage = new Stage(mainClass.viewport,mainClass.batch);
        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);


        final TextButton applybutton = new TextButton("Apply",mainClass.skin);
        applybutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainClass.jäägiLeidjaThread.interrupt();
                return true;
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.add(new Label("Jäägi leidmine võtab oodatust kauem aega.\nVahetuse peatamiseks klõpsake nupul.", labelStyle));
        table.row();
        table.add(applybutton);
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
        dispose();
    }

    @Override
    public void dispose() {
        mainClass.inputMultiplexer.removeProcessor(stage);
    }
}
