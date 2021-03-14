package gui.pangaautomaat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import gui.pangaautomaat.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainMenu implements Screen, TaskListener {
    private MainClass mainClass;
    private Stage stage;
    private Label kasutatudlabel;
    private int[] parimkasutatud;
    private int[] reference;
    private boolean isfinished;
    private double parimjääk;
    private boolean kuvatudLiigaKauaAegaTekst;
    private SelectBox<String> vahetustüüpbox;
    private TextField fromrahaarvtextfield;
    private TextField torahaarvtextfield;
    private SelectBox<String> fromrahaühikbox;
    private double vahetatavRaha;
    private SelectBox<String> torahaühikbox;
    private JäägiLeidjaRunnable jäägileidja;
    public MainMenu(final MainClass mainclass){
        this.mainClass = mainclass;
        reference = mainClass.prefs.getKupüüridInts();
        parimkasutatud = new int[reference.length];

        stage = new Stage(mainClass.viewport,mainClass.batch);

        Label.LabelStyle labelStyle = mainClass.skin.get(Label.LabelStyle.class);
        labelStyle.font = mainClass.assetsLoader.manager.get(mainClass.assetsLoader.robotoBlack);

        fromrahaarvtextfield = new TextField("0", mainClass.skin);
        torahaarvtextfield = new TextField("0", mainClass.skin);
        fromrahaühikbox = new SelectBox<>(mainClass.skin);
        torahaühikbox = new SelectBox<>(mainClass.skin);
        vahetustüüpbox = new SelectBox<>(mainClass.skin);

        final Label andmeteKuupäevText = new Label("andmete kuupäev: "+mainclass.dataDownloader.dataTime, labelStyle);
        kasutatudlabel = new Label("", labelStyle);
        fromrahaarvtextfield.setTextFieldFilter(new FloatTextFieldFilter());
        torahaarvtextfield.setTextFieldFilter(new FloatTextFieldFilter());
        torahaarvtextfield.setDisabled(true);
        fromrahaarvtextfield.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                muudaToTextFieldi();
            }
        });
        fromrahaühikbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeListener.ChangeEvent){
                    muudaToTextFieldi();
                }
                return true;
            }
        });
        torahaühikbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeListener.ChangeEvent){
                    muudaToTextFieldi();
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
                vahetus();
                return true;
            }
        });
        final TextButton downloadbutton = new TextButton("Download new data",mainClass.skin);
        downloadbutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainClass.dataDownloader.download();
                mainClass.updateDataToPrefs();
                andmeteKuupäevText.setText("andmete kuupäev: "+mainclass.dataDownloader.dataTime);
                return true;
            }
        });
        final TextButton laeautomaatibutton = new TextButton("Load machine",mainClass.skin);
        laeautomaatibutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainClass.setScreen(new LoadScreen(mainClass));
                return true;
            }
        });

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

        firstTable.row();
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
        mainTable.add(andmeteKuupäevText);
        mainTable.row();
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

    public void vahetus(){
        double rahasumma = Double.parseDouble(fromrahaarvtextfield.getText());
        String algvaluuta = fromrahaühikbox.getSelected();
        String soovvaluuta = torahaühikbox.getSelected();
        vahetatavRaha = valuutaVahetus(rahasumma,algvaluuta,soovvaluuta);
        int[] available = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(soovvaluuta));
        int[] kasutatud = new int[available.length];
        leiaParimJääk2(vahetatavRaha,available, kasutatud, reference);
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
                        parimkasutatud = kasutatud.clone();

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
                    isfinished = true;
                }
            }
        }
    }
    private void leiaVahetusMax(double rahasumma, int[] available, int[] kasutatud2, double parimjääk, int[] reference){
        leiaVahetusMin(rahasumma, available, kasutatud2,parimjääk, reference);
        available = makeAvailableFromKasutatud(parimkasutatud, available);
        boolean vahetusToimunud = true;
        while (vahetusToimunud){
            vahetusToimunud = false;
            for (int i = 1; i < parimkasutatud.length; i++) {
                if (parimkasutatud[i]>0){
                    for (int j = 1; j < i+1; j++) {
                        int lcm = LCM(new int[]{reference[i-j],reference[i]});
                        int vahetamiseksVajaPraegust = lcm/reference[i];
                        if (parimkasutatud[i]>=vahetamiseksVajaPraegust){
                            int vahetamiseksVajaVäiksemat = lcm/reference[i-j];
                            if (available[i-j]>=vahetamiseksVajaVäiksemat){
                                vahetusToimunud = true;
                                available[i-j]-=vahetamiseksVajaVäiksemat;
                                parimkasutatud[i-j] += vahetamiseksVajaVäiksemat;
                                available[i]+=vahetamiseksVajaPraegust;
                                parimkasutatud[i]-=vahetamiseksVajaPraegust;
                            }
                        }
                    }
                }
            }
        }
    }
    private int[] makeAvailableFromKasutatud(int[] kasutatud, int[] available){
        int[] answer = available.clone();
        for (int i = 0; i < answer.length; i++) {
            answer[i] -= kasutatud[i];
        }
        return answer;
    }
    private int leiaAutomaadisOlevRaha(int[] avaivable, int[] reference){
        int summa = 0;
        for (int i = 0; i < avaivable.length; i++) {
            summa+=avaivable[i]*reference[i];
        }
        return summa;
    }

    private void leiaParimJääk2(double rahasumma, int[] available, int[] kasutatud2, int[] reference) {
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
            this.parimjääk = rahasumma-leiaAutomaadisOlevRaha(available, reference);
            pealeJäägiThreadi();

        }else {
            double vähendatudRahaSumma = rahasumma%LCM(olemas);
            double eemaldatud = rahasumma-vähendatudRahaSumma;
            for (int i = reference.length-1; i > 0; i--) {
                while (available[i]>0 && eemaldatud>0){
                    eemaldatud-=reference[i];
                    available[i]--;
                }
            }
            jäägileidja = new JäägiLeidjaRunnable(vähendatudRahaSumma+eemaldatud,available,kasutatud2, reference, GCD(olemas), rahasumma);
            jäägileidja.addListener(this);
            mainClass.jäägiLeidjaThread = new Thread(jäägileidja);
            long start = System.nanoTime();
            long end;
            mainClass.jäägiLeidjaThread.start();
            kuvatudLiigaKauaAegaTekst = false;
            while (!kuvatudLiigaKauaAegaTekst && mainClass.jäägiLeidjaThread.isAlive()){
                end = System.nanoTime();
                if (end-start>=1000000000){
                    kuvatudLiigaKauaAegaTekst = true;
                    mainClass.setScreen(new WaitingScreen(mainClass));
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
        return paljuraha;
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
        double roundedPariumJääk = Math.round(parimjääk*20.0)/20.0;
        s+="jääk = "+roundedPariumJääk;
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

    @Override
    public void threadComplete(Runnable runner) {
        mainClass.setScreen(this);
        if (!jäägileidja.interrupted){
            this.parimjääk = jäägileidja.parimjääk;
            pealeJäägiThreadi();
        }
    }
    private void pealeJäägiThreadi(){
        isfinished = false;
        parimkasutatud = new int[reference.length];
        int[] tempreference = sortedList(reference);
        String soovvaluuta = torahaühikbox.getSelected();
        int[] available = mainClass.prefs.stringToInts(mainClass.prefs.preferences.getString(soovvaluuta));
        int[] availabletemp = matchReferenceWithAvaivable(tempreference, reference, available);
        int[] kasutatud = new int[available.length];
        String tagastatav = vahetustüüpbox.getSelected();
        if(tagastatav.equals("max")) {
            leiaVahetusMax(vahetatavRaha,availabletemp,kasutatud, parimjääk, tempreference);
        }
        if (tagastatav.equals("min")){
            leiaVahetusMin(vahetatavRaha, availabletemp, kasutatud, parimjääk, tempreference);
        }
        kuvaTagastatavText();
        int[] newSortedAvailable = makeAvailableFromKasutatud(parimkasutatud, availabletemp);
        available = matchReferenceWithAvaivable(reference, tempreference, newSortedAvailable);
        mainClass.prefs.preferences.putString(soovvaluuta,mainClass.prefs.intsToString(available));
    }
    private void muudaToTextFieldi(){
        if (fromrahaarvtextfield.getText().length()!=0){
            double rahasumma = valuutaVahetus(Double.parseDouble(fromrahaarvtextfield.getText()),fromrahaühikbox.getSelected(), torahaühikbox.getSelected());
            torahaarvtextfield.setText(String.valueOf(Math.round(rahasumma*20.0)/20.0));
        }else {
            torahaarvtextfield.setText("0");
        }
    }
}
