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
import java.util.HashMap;

public class MainMenu implements Screen {
    private MainClass mainClass;
    private Stage stage;
    private Label kasutatudlabel;
    private int[] parimkasutatud;
    private float parimrahasumma;
    private int[] reference;
    private int vähimx;
    public MainMenu(MainClass mainclass){
        this.mainClass = mainclass;
    }

    @Override
    public void show() {
        stage = new Stage(mainClass.viewport,mainClass.batch);
        reference = mainClass.prefs.getKupüüridInts();
        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);
        //reference = mainClass.prefs.getKupüüridInts();
        reference = new int[]{1000,500,50,20,2};

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
                //TODO KIRJUTA SIIA OMA KOOD, KUI RAHAVAHETUS
                //START

                vahetus(vahetustüüpbox.getSelected(), Float.parseFloat(rahaarvtextfield.getText()), fromrahaühikbox.getSelected(), torahaühikbox.getSelected());


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

    }

    public float[] vahetus(String tagastatav, float rahasumma, String algvaluuta, String soovvaluuta) {
        String eurprefix = mainClass.prefs.EURPREFIX;
        float algkurss = mainClass.prefs.preferences.getFloat(eurprefix + algvaluuta);
        float soovkurss = mainClass.prefs.preferences.getFloat(eurprefix + soovvaluuta);
        float realkurss = soovkurss/algkurss;
        float paljuraha = rahasumma * realkurss;
        //TODO check, et pangaautomaadis yldse piisavalt raha vahetuseks
        int[] available = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(soovvaluuta));
        int[] kasutatud = new int[available.length];
        for (int i = 0; i < available.length; i++) {
            vähimx += available[i];
        }
        // available[] index tähistab: [1000, 500, 200, 100, 50, 20]
        parimkasutatud = new int[reference.length];
        parimrahasumma = rahasumma;
        //System.out.println(Arrays.toString(reference));
        float remaining = paljuraha;
        /*
        Samm 1 - Kõik fucking kombinatsioonid
        Samm 2 - Kas mõni on täpne?
        >>> Ei - võta täpseim >>> Samm 3
        >>> Jah - kui mitu - Samm 3, else - võta see üks
        Samm 3 - min või max? võta sobiv
         */
        /*
        if (tagastatav.equals("max")) {
            for (int i = 0; i < available.length; i++) {
                while (remaining >= reference[i] && kasutatud[i] != available[i]) {
                    kasutatud[i] ++;
                    remaining -= reference[i];
                }
            }
        } else if (tagastatav.equals("min")) {
            for (int i = available.length-1; i >= 0; i--) {
                //System.out.print("i: ");
                //System.out.println(i);
                while (remaining >= reference[i] && kasutatud[i] != available[i]) {
                    kasutatud[i]++;
                    remaining -= reference[i];
                }
            }
        }

         */
        //rec(rahasumma, available, kasutatud, 0);
        //leiaParimVahetus(rahasumma, available, kasutatud, 0);
        double parimjääk = leiaParimJääk();
        rec(rahasumma, available, kasutatud, 0);
        //System.out.println(Arrays.toString(parimkasutatud));
        //rec2(rahasumma,available,kasutatud,1,parimjääk);
        System.out.println("-------------");
        String[] kupyyrid = mainClass.prefs.getKupüürid().split(",");
        String s = "";
        for (int i = 0; i < kupyyrid.length; i++) {
            s += kupyyrid[i] + "-" + kasutatud[i] + "\n";
        }

        //System.out.println(s);
        kasutatudlabel.setText(s);

        return new float[] {paljuraha, remaining};
    }

    public void rec(float rahasumma, int[] available, int[] kasutatud2, int x) {
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }
        //System.out.print(x + " --- ");
        //System.out.print(rahasumma + " -- ");
        //System.out.println(Arrays.toString(kasutatud));

        //Oletame, et min
        /*
        if (rahasumma <= 0) {

            return kasutatud;
        }
         */
        for (int i = 0; i < kasutatud.length; i++) {
            if (rahasumma >= reference[i]) {
                available[i] -= 1;
                kasutatud[i] += 1;
                rahasumma -= reference[i];
                if (rahasumma < parimrahasumma&&x < vähimx) {
                    parimrahasumma = rahasumma;
                    parimkasutatud = kasutatud;
                    //vähimx = x;
                    System.out.print(x + " --- ");
                    System.out.print(rahasumma + " -- ");
                    System.out.println(Arrays.toString(kasutatud));
                }
                rec(rahasumma, available, kasutatud, x+1);
                available[i] += 1;
                kasutatud[i] -= 1;
                rahasumma += reference[i];
            }
        }
    }

    private void rec2(float rahasumma, int[] available, int[] kasutatud2, int x, double parimjääk){
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }
        for (int i = kasutatud.length-1; i >= 0; i--) {
            if (rahasumma >= reference[i]) {
                available[i] -= 1;
                kasutatud[i] += 1;
                rahasumma -= reference[i];
                if (rahasumma < parimjääk) {
                    parimkasutatud = kasutatud;
                    System.out.print(x + " --- ");
                    System.out.print(rahasumma + " -- ");
                    System.out.println(Arrays.toString(kasutatud));
                }
                rec2(rahasumma, available, kasutatud, x + 1,parimjääk);
                available[i] += 1;
                kasutatud[i] -= 1;
                rahasumma += reference[i];
            }
        }
    }
    private double leiaParimJääk(){
        int i1 = reference.length-1;
        int i2 = reference.length-2;
        double a = reference[i2]/reference[i1];
        int a1 = (int) a;
        int a2 = a1+1;
        return Math.max(reference[i1]*a2 - reference[i2], reference[i2]-reference[i1] * a1);
    }
}
