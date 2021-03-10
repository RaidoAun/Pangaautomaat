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

public class MainMenu implements Screen {
    private MainClass mainClass;
    private Stage stage;
    public MainMenu(MainClass mainclass){
        this.mainClass = mainclass;
    }

    @Override
    public void show() {
        stage = new Stage(mainClass.viewport,mainClass.batch);

        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);


        final TextField rahaarvtextfield = new TextField("", mainClass.skin);
        rahaarvtextfield.setTextFieldFilter(new NumberTextFieldFilter());

        final SelectBox<String> rahaühikbox = new SelectBox<>(mainClass.skin);
        String[] values = new String[5];
        values[0] = "USA dollar USD";
        values[1] = "inglise nael GBP";
        values[2] = "sveitsi frank CHF";
        values[3] = "rootsi kroon SEK";
        values[4] = "vene rubla RUB";
        rahaühikbox.setItems(values);

        final SelectBox<String> vahetustüüpbox = new SelectBox<>(mainClass.skin);
        values = new String[2];
        values[0] = "min";
        values[1] = "max";
        vahetustüüpbox.setItems(values);

        final TextButton applybutton = new TextButton("Apply",mainClass.skin);
        applybutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //TODO KIRJUTA SIIA OMA KOOD, KUI RAHAVAHETUS
                return true;
            }
        });
        final TextButton downloadbutton = new TextButton("Download new data",mainClass.skin);
        downloadbutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainClass.dataDownloader.download();
                mainClass.updateDataToPrefs();
                //TODO GUI võiks näidata ka praeguseid kursi andmeid, siin saaks uuendada seda
                //mainClass.dataDownloader.dataTime;
                //mainClass.prefs.getEUR_USA();
                return true;
            }
        });
        Table table = new Table();
        table.setFillParent(true);
        table.add(new Label("Sisesta rahasumma: ",labelStyle));
        table.add(rahaarvtextfield);
        table.add(rahaühikbox);
        table.add(vahetustüüpbox);
        table.row();
        table.add(applybutton);
        table.add(downloadbutton);
        stage.addActor(table);
        mainClass.inputMultiplexer.setProcessors(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
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

    }
}
