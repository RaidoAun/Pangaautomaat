package gui.pangaautomaat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Prefs {

    private final String USA_DOLLAR = "USA dollar";
    private final String INGLISE_NAEL = "inglise nael";
    private final String SVEITSI_FRANK = "šveitsi frank";
    private final String ROOTSI_KROON = "rootsi kroon";
    private final String VENE_RUBLA = "vene rubla";

    private final String EUR_USA = MainClass.valuutad[0];
    private final String EUR_NAEL = MainClass.valuutad[1];
    private final String EUR_SVEITS = MainClass.valuutad[2];
    private final String EUR_ROOTSI = MainClass.valuutad[3];
    private final String EUR_Rubla = MainClass.valuutad[4];
    private final String defaultKupüürid = "30,200,150,300,600";

    private final String KUPÜÜRID = "Kupüürid";

    public Preferences preferences;

    public Prefs(){
        this.preferences = Gdx.app.getPreferences("PangaautomaatPrefs");
    }

    public void save(){
        this.preferences.flush();
    }

    public String getKupüürid(){
        return this.preferences.getString(this.KUPÜÜRID);
    }
    public void setKupüürid(String s){
        this.preferences.putString(this.KUPÜÜRID, s);
    }
    public int[] getKupüüridInts(){
        return stringToInts(getKupüürid());
    }


    public String getUSADollar() {
        return this.preferences.getString(this.USA_DOLLAR);
    }
    public String getIngliseNael() {
        return this.preferences.getString(this.INGLISE_NAEL);
    }
    public String getSveitsiFrank() {
        return this.preferences.getString(this.SVEITSI_FRANK);
    }
    public String getRootsiKroon() {
        return this.preferences.getString(this.ROOTSI_KROON);
    }
    public String getVeneRubla() {
        return this.preferences.getString(this.VENE_RUBLA);
    }

    public int[] getUSADollarInt(){
        return stringToInts(getUSADollar());
    }
    public int[] getIngliseNaelInt(){
        return stringToInts(getIngliseNael());
    }
    public int[] getSveitsiFrankInt(){
        return stringToInts(getSveitsiFrank());
    }
    public int[] getRootsiKroonInt(){
        return stringToInts(getRootsiKroon());
    }
    public int[] getVeneRublaInt(){
        return stringToInts(getVeneRubla());
    }

    public float getEUR_USA(){
        return this.preferences.getFloat(this.EUR_USA);
    }
    public float getEUR_Nael(){
        return this.preferences.getFloat(this.EUR_NAEL);
    }
    public float getEUR_Sveits(){
        return this.preferences.getFloat(this.EUR_SVEITS);
    }
    public float getEUR_Rootsi(){
        return this.preferences.getFloat(this.EUR_ROOTSI);
    }
    public float getEUR_Rubla(){
        return this.preferences.getFloat(this.EUR_Rubla);
    }

    public void setEUR_USA(float f){
        this.preferences.putFloat(this.EUR_USA, f);
    }
    public void setEUR_NAEL(float f){
        this.preferences.putFloat(this.EUR_NAEL, f);
    }
    public void setEUR_SVEITS(float f){
        this.preferences.putFloat(this.EUR_SVEITS, f);
    }
    public void setEUR_ROOTSI(float f){
        this.preferences.putFloat(this.EUR_ROOTSI, f);
    }
    public void setEUR_Rubla(float f){
        this.preferences.putFloat(this.EUR_Rubla, f);
    }


    public void setUSADollar(String s){
        this.preferences.putString(this.USA_DOLLAR, s);
    }
    public void setIngliseNael(String s){
        this.preferences.putString(this.INGLISE_NAEL, s);
    }
    public void setSveitsiFrank(String s){
        this.preferences.putString(this.SVEITSI_FRANK, s);
    }
    public void setRootsiKroon(String s){
        this.preferences.putString(this.ROOTSI_KROON, s);
    }
    public void setVeneRubla(String s){
        this.preferences.putString(this.VENE_RUBLA, s);
    }

    public void init(){
        boolean toSave = false;
        if (this.preferences.get().isEmpty()){

        }
        if (!this.preferences.get().containsKey(this.USA_DOLLAR)){
            setUSADollar(defaultKupüürid);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.INGLISE_NAEL)){
            setIngliseNael(defaultKupüürid);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.SVEITSI_FRANK)){
            setSveitsiFrank(defaultKupüürid);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.ROOTSI_KROON)){
            setRootsiKroon(defaultKupüürid);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.VENE_RUBLA)){
            setVeneRubla(defaultKupüürid);
            toSave = true;
        }

        if (!this.preferences.get().containsKey(this.EUR_USA)){
            setEUR_USA(1.0f);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.EUR_NAEL)){
            setEUR_NAEL(1.0f);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.EUR_SVEITS)){
            setEUR_SVEITS(1.0f);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.EUR_ROOTSI)){
            setEUR_ROOTSI(1.0f);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.EUR_Rubla)){
            setEUR_Rubla(1.0f);
            toSave = true;
        }
        if (!this.preferences.get().containsKey(this.KUPÜÜRID)){
            setKupüürid("1000,500,200,50,20");
            toSave = true;
        }
        if (toSave){
            save();
        }
    }

    public int[] stringToInts(String string){
        String[] strings = string.split(",");
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }
}
