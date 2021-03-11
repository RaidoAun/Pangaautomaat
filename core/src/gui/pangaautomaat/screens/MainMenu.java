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
import java.util.concurrent.atomic.AtomicBoolean;

public class MainMenu implements Screen {
    private MainClass mainClass;
    private Stage stage;
    private Label kasutatudlabel;
    private int[] parimkasutatud;
    private double parimrahasumma;
    private int[] reference;
    private AtomicBoolean isfinished;
    public MainMenu(MainClass mainclass){
        this.mainClass = mainclass;
    }

    @Override
    public void show() {
        stage = new Stage(mainClass.viewport,mainClass.batch);
        reference = mainClass.prefs.getKupüüridInts();
        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);
        reference = mainClass.prefs.getKupüüridInts();
        //reference = new int[]{1000,500,200,50,20};

        final TextField rahaarvtextfield = new TextField("0", mainClass.skin);
        rahaarvtextfield.setTextFieldFilter(new NumberTextFieldFilter());

        final SelectBox<String> fromrahaühikbox = new SelectBox<>(mainClass.skin);
        String[] values = new String[5];
        values[0] = "USD";
        values[1] = "GBP";
        values[2] = "CHF";
        values[3] = "SEK";
        values[4] = "RUB";
        fromrahaühikbox.setItems(values);

        final SelectBox<String> torahaühikbox = new SelectBox<>(mainClass.skin);
        torahaühikbox.setItems(values);

        final SelectBox<String> vahetustüüpbox = new SelectBox<>(mainClass.skin);
        values = new String[2];
        values[0] = "min";
        values[1] = "max";
        vahetustüüpbox.setItems(values);

        final TextButton applybutton = new TextButton("Apply",mainClass.skin);
        applybutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //START
                vahetus(vahetustüüpbox.getSelected(), Double.parseDouble(rahaarvtextfield.getText()), fromrahaühikbox.getSelected(), torahaühikbox.getSelected());


                //FINISH
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
        kasutatudlabel = new Label("", labelStyle);
        Table table = new Table();
        table.setFillParent(true);
        table.add(new Label("Sisesta rahasumma: ",labelStyle));
        table.add(rahaarvtextfield);
        table.add(fromrahaühikbox);
        table.add(torahaühikbox);
        table.add(vahetustüüpbox);
        table.row();
        table.add(applybutton);
        table.add(downloadbutton);
        table.row();
        table.add(kasutatudlabel);
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
        stage.dispose();
    }

    public void vahetus(String tagastatav, double rahasumma, String algvaluuta, String soovvaluuta) {
        String eurprefix = mainClass.prefs.EURPREFIX;
        double algkurss = mainClass.prefs.preferences.getFloat(eurprefix + algvaluuta);
        double soovkurss = mainClass.prefs.preferences.getFloat(eurprefix + soovvaluuta);
        double realkurss = soovkurss/algkurss;
        double paljuraha = rahasumma * realkurss;
        isfinished = new AtomicBoolean();
        isfinished.set(false);
        //TODO check, et pangaautomaadis yldse piisavalt raha vahetuseks
        int[] available = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(soovvaluuta));
        int[] kasutatud = new int[available.length];
        // available[] index tähistab: [1000, 500, 200, 50, 20]
        parimkasutatud = new int[reference.length];
        double parimjääk = leiaParimJääk(rahasumma, available);
        if(tagastatav.equals("max")) {
            leiaVahetusMax(rahasumma,available,kasutatud, parimjääk);
        }
        if (tagastatav.equals("min")){
            leiaVahetusMin(rahasumma, available, kasutatud, parimjääk);
        }


        System.out.println("-------------");
        String[] kupyyrid = mainClass.prefs.getKupüürid().split(",");
        String s = "";
        for (int i = 0; i < kupyyrid.length; i++) {
            s += kupyyrid[i] + "-" + kasutatud[i] + "\n";
        }
        kasutatudlabel.setText(s);
    }

    public void leiaVahetusMin(double rahasumma, int[] available, int[] kasutatud2, double parimjääk) {
        if (isfinished.get()) {
            return;
        }
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }
        for (int i = 0; i < kasutatud.length; i++) {
            if (rahasumma >= reference[i]) {
                if (available[i]>0){
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    rahasumma -= reference[i];
                    if (rahasumma == parimjääk) {
                        parimrahasumma = rahasumma;
                        parimkasutatud = kasutatud;
                        System.out.print(rahasumma + " -- ");
                        System.out.println(Arrays.toString(kasutatud));
                        isfinished.set(true);
                    }
                    leiaVahetusMin(rahasumma, available, kasutatud, parimjääk);
                    available[i] += 1;
                    kasutatud[i] -= 1;
                    rahasumma += reference[i];
                }
            }
        }
    }
    private void leiaVahetusMax(double rahasumma, int[] available, int[] kasutatud2, double parimjääk){
        if (isfinished.get()) {
            return;
        }
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }
        for (int i = kasutatud.length-1; i >= 0; i--) {
            if (rahasumma >= reference[i]) {
                if (available[i]>0){
                    if (isfinished.get()){
                        return;
                    }
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    rahasumma -= reference[i];
                    if (rahasumma == parimjääk) {
                        parimrahasumma = rahasumma;
                        parimkasutatud = kasutatud;
                        System.out.print(rahasumma + " -- ");
                        System.out.println(Arrays.toString(kasutatud));
                        isfinished.set(true);
                    }
                    leiaVahetusMax(rahasumma, available, kasutatud, parimjääk);
                    available[i] += 1;
                    kasutatud[i] -= 1;
                    rahasumma += reference[i];
                }
            }
        }
    }
    private double leiaParimJääk(double rahasumma, int[] avaivable){

        int automaadisRaha = leiaAutomaadisOlevRaha(avaivable);

        int i1 = reference.length-1; //20
        int i2 = reference.length-2; //50

        if (rahasumma < reference[i1]) {
            return rahasumma;
        } else {
            double a = reference[i2] / reference[i1];
            int a1 = (int) a;
            int a2 = a1 + 1;
            float algjääk = Math.max(reference[i1] * a2 - reference[i2], reference[i2] - reference[i1] * a1);
            double parimjääk = rahasumma % reference[i1];
            if (parimjääk >= algjääk && rahasumma >= reference[i2]) {
                parimjääk -= algjääk;
            }
            int kahtlaseidKohti = reference[i2]/reference[i1];
            for (int i = 0; i < reference[i2]/reference[i1]; i++) {
                float aluminepiir =automaadisRaha-kahtlaseidKohti*reference[i1]+algjääk+reference[i1]*i;
                float üleminepiir =aluminepiir+algjääk;
                if (üleminepiir>rahasumma && rahasumma>=aluminepiir) {
                    return algjääk + parimjääk;
                }
            }
            if (rahasumma>=automaadisRaha){
                return rahasumma-automaadisRaha;
            }
            return parimjääk;
        }
    }
    private int leiaAutomaadisOlevRaha(int[] avaivable){
        int summa = 0;
        for (int i = 0; i < avaivable.length; i++) {
            summa+=avaivable[i]*reference[i];
        }
        return summa;
    }
}
