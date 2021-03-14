package gui.pangaautomaat;

public class JäägiLeidjaRunnable extends NotificationThread{
    private double rahasumma;
    private int[] available;
    private int[] kasutatud2;
    private int[] reference;
    private int gcd;
    public double parimjääk;
    public boolean interrupted;
    public JäägiLeidjaRunnable(double rahasumma, int[] available, int[] kasutatud2, int[] reference, int gcd, double parimjääk){
        this.rahasumma = rahasumma;
        this.available = available;
        this.kasutatud2 = kasutatud2;
        this.reference = reference;
        this.gcd = gcd;
        this.parimjääk = parimjääk;
        this.interrupted = false;
    }
    private double leiaParimJääkRekursiooniga(double rahasumma, int[] available, int[] kasutatud2, int[] reference, int gcd, double parimjääk){
        if (Thread.interrupted()){
            this.interrupted = true;
            return -1;
        }
        if (parimjääk < gcd || parimjääk == 0){
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

    @Override
    public void doWork() {
        parimjääk = leiaParimJääkRekursiooniga(rahasumma, available,kasutatud2, reference, gcd, parimjääk);
    }
}
