package gui.pangaautomaat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import gui.pangaautomaat.FloatTextFieldFilter;
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
    private boolean isfinished;
    public MainMenu(MainClass mainclass){
        this.mainClass = mainclass;
        stage = new Stage(mainClass.viewport,mainClass.batch);
        reference = mainClass.prefs.getKupüüridInts();
        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);
        reference = mainClass.prefs.getKupüüridInts();
        //reference = new int[]{1000,500,200,50,20};

        final TextField fromrahaarvtextfield = new TextField("0", mainClass.skin);
        final TextField torahaarvtextfield = new TextField("0", mainClass.skin);
        final SelectBox<String> fromrahaühikbox = new SelectBox<>(mainClass.skin);
        final SelectBox<String> vahetustüüpbox = new SelectBox<>(mainClass.skin);
        final SelectBox<String> torahaühikbox = new SelectBox<>(mainClass.skin);
        fromrahaarvtextfield.setTextFieldFilter(new FloatTextFieldFilter());
        torahaarvtextfield.setTextFieldFilter(new FloatTextFieldFilter());
        torahaarvtextfield.setDisabled(true);
        fromrahaarvtextfield.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (fromrahaarvtextfield.getText().length()!=0){
                    torahaarvtextfield.setText(String.valueOf(valuutaVahetus(Double.parseDouble(fromrahaarvtextfield.getText()),fromrahaühikbox.getSelected(), torahaühikbox.getSelected())));
                }else {
                    torahaarvtextfield.setText("0");
                }
            }
        });
        fromrahaühikbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeListener.ChangeEvent){
                    if (fromrahaarvtextfield.getText().length()!=0){
                        torahaarvtextfield.setText(String.valueOf(valuutaVahetus(Double.parseDouble(fromrahaarvtextfield.getText()),fromrahaühikbox.getSelected(), torahaühikbox.getSelected())));
                    }else {
                        torahaarvtextfield.setText("0");
                    }
                }
                return true;
            }
        });
        torahaühikbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeListener.ChangeEvent){
                    if (fromrahaarvtextfield.getText().length()!=0){
                        torahaarvtextfield.setText(String.valueOf(valuutaVahetus(Double.parseDouble(fromrahaarvtextfield.getText()),fromrahaühikbox.getSelected(), torahaühikbox.getSelected())));
                    }else {
                        torahaarvtextfield.setText("0");
                    }
                }
                return true;
            }
        });

        String[] values = new String[5];
        values[0] = "USD";
        values[1] = "GBP";
        values[2] = "CHF";
        values[3] = "SEK";
        values[4] = "RUB";
        fromrahaühikbox.setItems(values);
        torahaühikbox.setItems(values);

        values = new String[2];
        values[0] = "min";
        values[1] = "max";
        vahetustüüpbox.setItems(values);

        final TextButton applybutton = new TextButton("Apply",mainClass.skin);
        applybutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //START
                vahetus(vahetustüüpbox.getSelected(), Double.parseDouble(fromrahaarvtextfield.getText()), fromrahaühikbox.getSelected(), torahaühikbox.getSelected());


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
        final TextButton laeautomaatibutton = new TextButton("Load machine",mainClass.skin);
        laeautomaatibutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainClass.setScreen(new LoadScreen(mainClass));
                //dispose();
                return true;
            }
        });
        kasutatudlabel = new Label("", labelStyle);
        String[] kupyyrid = mainClass.prefs.getKupüürid().split(",");
        String s = "";
        for (int i = 0; i < kupyyrid.length; i++) {
            s += kupyyrid[i] + "-" + "0" + "\n";
        }
        s+="summa = 0";
        kasutatudlabel.setText(s);

        Table firstTable = new Table();
        firstTable.add(fromrahaühikbox);
        firstTable.add(fromrahaarvtextfield);
        firstTable.add(vahetustüüpbox);
        firstTable.row();
        firstTable.add(torahaühikbox);
        firstTable.add(torahaarvtextfield);
        firstTable.row();
        firstTable.add(applybutton).colspan(3).pad(10);
        Table secondaryTable = new Table().bottom();
        secondaryTable.row();
        secondaryTable.add(downloadbutton).fillY().bottom();
        secondaryTable.row();
        secondaryTable.add(laeautomaatibutton);
        secondaryTable.row();
        secondaryTable.add(kasutatudlabel);
        Table mainTable = new Table();
        mainTable.setHeight(600);
        mainTable.setFillParent(true);
        mainTable.add(firstTable).top();
        mainTable.row();
        mainTable.add(secondaryTable).minHeight(300).bottom();
        stage.addActor(mainTable);

    }

    @Override
    public void show() {
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
        mainClass.inputMultiplexer.removeProcessor(stage);

    }

    @Override
    public void dispose() {
        mainClass.inputMultiplexer.removeProcessor(stage);
        stage.dispose();
    }

    public void vahetus(String tagastatav, double rahasumma, String algvaluuta, String soovvaluuta) {
        double paljuraha = valuutaVahetus(rahasumma,algvaluuta,soovvaluuta);
        isfinished = false;
        //TODO check, et pangaautomaadis yldse piisavalt raha vahetuseks
        int[] available = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(soovvaluuta));
        int[] kasutatud = new int[available.length];
        // available[] index tähistab: [1000, 500, 200, 50, 20]
        parimkasutatud = new int[reference.length];
        double parimjääk = leiaParimJääk(paljuraha, available);
        if(tagastatav.equals("max")) {
            leiaVahetusMax(paljuraha,available,kasutatud, parimjääk);
        }
        if (tagastatav.equals("min")){
            leiaVahetusMin(paljuraha, available, kasutatud, parimjääk);
        }

    }

    public void leiaVahetusMin(double rahasumma, int[] available, int[] kasutatud2, double parimjääk) {
        if (isfinished) {
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
                        parimkasutatud = kasutatud;
                        kuvaTagastatavText();
                        isfinished = true;
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
        if (isfinished) {
            return;
        }
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }
        for (int i = kasutatud.length-1; i >= 0; i--) {
            if (rahasumma >= reference[i]) {
                if (available[i]>0){
                    if (isfinished){
                        return;
                    }
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    rahasumma -= reference[i];
                    if (rahasumma == parimjääk) {
                        parimrahasumma = rahasumma;
                        parimkasutatud = kasutatud;
                        kuvaTagastatavText();
                        isfinished = true;
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
                    //return algjääk + parimjääk;
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
    private void leiaParimJääkRekursiooniga(double rahasumma, int[] available, int[] kasutatud2, double parimjääk){
        int[] vähimadkordsed = new int[]{50,20};
        int[] ülejäänud = new int[]{1000,500,200};
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }
        for (int i = 0; i < ülejäänud.length; i++) {
            if (rahasumma >= ülejäänud[i]) {
                if (available[i]>0){
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    rahasumma -= ülejäänud[i];
                    if (rahasumma == parimjääk) {
                        parimkasutatud = kasutatud;
                        System.out.print(rahasumma + " -- ");
                        System.out.println(Arrays.toString(kasutatud));
                        isfinished = true;
                    }
                    leiaVahetusMin(rahasumma, available, kasutatud, parimjääk);
                    available[i] += 1;
                    kasutatud[i] -= 1;
                    rahasumma += reference[i];
                }
            }
        }
    }
    private double valuutaVahetus(double rahasumma, String algvaluuta, String soovvaluuta){
        String eurprefix = mainClass.prefs.EURPREFIX;
        double algkurss = mainClass.prefs.preferences.getFloat(eurprefix + algvaluuta);
        double soovkurss = mainClass.prefs.preferences.getFloat(eurprefix + soovvaluuta);
        double realkurss = soovkurss/algkurss;
        double paljuraha = (rahasumma * realkurss);
        return Math.round(paljuraha*20)/20.0;
    }
    private void kuvaTagastatavText(){
        String[] kupyyrid = mainClass.prefs.getKupüürid().split(",");
        String s = "";
        int summa = 0;
        for (int i = 0; i < kupyyrid.length; i++) {
            s += kupyyrid[i] + " x " + parimkasutatud[i] + "\n";
            summa+=parimkasutatud[i]*reference[i];
        }
        s+="summa = "+summa;
        kasutatudlabel.setText(s);
    }
}
