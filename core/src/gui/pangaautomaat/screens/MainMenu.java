package gui.pangaautomaat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import gui.pangaautomaat.FloatTextFieldFilter;
import gui.pangaautomaat.MainClass;
import gui.pangaautomaat.NumberTextFieldFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainMenu implements Screen {
    private MainClass mainClass;
    private Stage stage;
    private Label kasutatudlabel;
    private int[] parimkasutatud;
    private double parimrahasumma;
    private int[] reference;
    private boolean isfinished;
    private double parimjääk;
    public MainMenu(MainClass mainclass){
        this.mainClass = mainclass;
        reference = mainClass.prefs.getKupüüridInts();
        parimkasutatud = new int[reference.length];

        stage = new Stage(mainClass.viewport,mainClass.batch);

        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);

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
        Arrays.sort(kupyyrid, new Comparator<String>()
        {
            public int compare(String s1, String s2)
            {
                return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
            }
        });
        kuvaTagastatavText();

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
        reference = mainClass.prefs.getKupüüridInts();

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
        //double parimjääk = leiaParimJääk(paljuraha, available);
        int[] tempreference = sortedList(reference);
        int[] avaivabletemp = matchReferenceWithAvaivable(tempreference, reference, available);
        double parimjääk = leiaParimJääk2(paljuraha,available, kasutatud, reference);
        this.parimjääk = parimjääk;
        System.out.println(parimjääk);
        if(tagastatav.equals("max")) {
            leiaVahetusMax(paljuraha,avaivabletemp,kasutatud, parimjääk, tempreference);
        }
        if (tagastatav.equals("min")){
            leiaVahetusMin(paljuraha, avaivabletemp, kasutatud, parimjääk, tempreference);
        }
        System.out.println("-------");
    }

    public void leiaVahetusMin(double rahasumma, int[] available, int[] kasutatud2, double parimjääk, int[] reference) {
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
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    rahasumma -= reference[i];
                    if (rahasumma == parimjääk) {
                        parimkasutatud = kasutatud;
                        kuvaTagastatavText();
                        isfinished = true;
                    }
                    leiaVahetusMin(rahasumma, available, kasutatud, parimjääk, reference);
                    available[i] += 1;
                    kasutatud[i] -= 1;
                    rahasumma += reference[i];
                }
            }else {
                if (rahasumma == parimjääk) {
                    parimkasutatud = kasutatud;
                    kuvaTagastatavText();
                    isfinished = true;
                }
            }
        }
    }
    private void leiaVahetusMax(double rahasumma, int[] available, int[] kasutatud2, double parimjääk, int[] reference){
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
                    if (isfinished){
                        return;
                    }
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    System.out.println(Arrays.toString(kasutatud));
                    System.out.println(rahasumma);
                    System.out.println("-----");
                    rahasumma -= reference[i];
                    if (rahasumma == parimjääk) {
                        parimkasutatud = kasutatud;
                        kuvaTagastatavText();
                        isfinished = true;
                    }

                    leiaVahetusMax(rahasumma, available, kasutatud, parimjääk, reference);
                    available[i] += 1;
                    kasutatud[i] -= 1;
                    rahasumma += reference[i];
                }
            }else {
                if (rahasumma == parimjääk) {
                    parimkasutatud = kasutatud;
                    kuvaTagastatavText();
                    isfinished = true;
                }
            }
        }
    }
    private int leiaAutomaadisOlevRaha(int[] avaivable, int[] reference){
        int summa = 0;
        for (int i = 0; i < avaivable.length; i++) {
            summa+=avaivable[i]*reference[i];
        }
        return summa;
    }

    private double leiaParimJääk2(double rahasumma, int[] available, int[] kasutatud2, int[] reference){
        ArrayList<Integer> olemasKupüürid = new ArrayList<>();
        for (int i = 0; i < available.length; i++) {
            if (available[i]!=0){
                olemasKupüürid.add(reference[i]);
            }
        }
        int[] olemas = new int[olemasKupüürid.size()];
        for (int i = 0; i < olemasKupüürid.size(); i++) {
            olemas[i] = olemasKupüürid.get(i);
        }
        int [] tempreference = sortedList(reference);
        available = matchReferenceWithAvaivable(tempreference, reference, available);
        reference = tempreference;
        if (leiaAutomaadisOlevRaha(available, reference)<=rahasumma){
            System.out.println("AUTOMAADIS POLE PIISAVALT RAHA");
            return rahasumma-leiaAutomaadisOlevRaha(available, reference);

        }else {
            double vähendatudRahaSumma = rahasumma%LCM(olemas);
            double eemaldatud = rahasumma-vähendatudRahaSumma;
            //TODO jääk on raudselt 0, võib kasutada leiaVahetusMin();
            for (int i = reference.length-1; i > 0; i--) {
                while (available[i]>0 && eemaldatud>=0){
                    System.out.println(eemaldatud);
                    eemaldatud-=reference[i];
                    available[i]--;
                }
            }
            System.out.println(vähendatudRahaSumma);
            System.out.println(Arrays.toString(available));
            return leiaParimJääkRekursiooniga(vähendatudRahaSumma+eemaldatud,available,kasutatud2, reference, GCD(olemas), rahasumma);

        }



    }

    private double leiaParimJääkRekursiooniga(double rahasumma, int[] available, int[] kasutatud2, int[] reference, int gcd, double parimjääk){
        if (parimjääk <gcd){
            return parimjääk;
        }
        int[] kasutatud = new int[kasutatud2.length];
        for (int i = 0; i < kasutatud2.length; i++) {
            kasutatud[i] = kasutatud2[i];
        }

        for (int i = 0; i < reference.length; i++) {
            if (rahasumma >= reference[i]) {
                if (available[i]>0){
                    available[i] -= 1;
                    kasutatud[i] += 1;
                    rahasumma -= reference[i];
                    parimjääk = leiaParimJääkRekursiooniga(rahasumma, available, kasutatud, reference, gcd, parimjääk);
                    available[i] += 1;
                    kasutatud[i] -= 1;
                    rahasumma += reference[i];
                }
            }else {
                if (rahasumma<parimjääk){
                    parimjääk = rahasumma;
                }
            }
        }
        return parimjääk;
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
        Arrays.sort(kupyyrid, new Comparator<String>()
        {
            public int compare(String s1, String s2)
            {
                return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
            }
        });
        String s = "";
        int summa = 0;
        int[] sortedReference = sortedList(reference);
        for (int i = 0; i < kupyyrid.length; i++) {
            s += kupyyrid[i] + " x " + parimkasutatud[i] + "\n";

            summa+=parimkasutatud[i]*sortedReference[i];
        }
        s+="summa = "+summa+"\n";
        s+="jääk = "+parimjääk;
        kasutatudlabel.setText(s);
    }
    private int LCMRecursion(int[] ints, int biggest){
        if (ints.length==1){
            return biggest;
        }

        while (biggest%ints[ints.length-2]!=0){
            biggest+=ints[ints.length-1];
        }
        int[] newInts = new int[ints.length-1];
        for (int i = 0; i < newInts.length-1; i++) {
            newInts[i] = ints[i];
        }
        newInts[newInts.length-1] = biggest;
        return LCMRecursion(newInts, biggest);
    }
    private int LCM(int[] ints){
        int[] clone = ints.clone();
        Arrays.sort(clone);
        return LCMRecursion(clone, clone[clone.length-1]);
    }
    private int[] matchReferenceWithAvaivable(int[] sortedReference, int[] reference, int[] avaivable){
        int[] answer = new int[avaivable.length];
        for (int i = 0; i < reference.length; i++) {
            for (int j = 0; j < reference.length; j++) {
                if (sortedReference[j] == reference[i]){
                    answer[j] = avaivable[i];
                }
            }
        }
        return answer;
    }
    private int[] sortedList(int[] ints){
        int[] a = ints.clone();
        Arrays.sort(a);
        return a;
    }

    private int GCD(int[] ints){
        int[] clone = ints.clone();
        Arrays.sort(clone);
        return GCDRecursion(clone);
    }
    private int GCDRecursion(int[] ints){
        if (ints.length==1){
            return ints[0];
        }
        int n1 = ints[ints.length-1];
        int n2 = ints[ints.length-2];

        while(n1 != n2)
        {
            if(n1 > n2)
                n1 -= n2;
            else
                n2 -= n1;
        }
        int[] newInts = new int[ints.length-1];
        for (int i = 0; i < newInts.length-1; i++) {
            newInts[i] = ints[i];
        }
        newInts[newInts.length-1] = n1;
        return GCD(newInts);
    }
}
