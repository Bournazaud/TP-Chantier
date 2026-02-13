public class Electricien extends Thread {

    public Electricien(String nom) {
        super(nom);
    }


    public void travailler() {
        System.out.println(getName() + " (Electricien) : Je commence à travailler.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " (Electricien) : J'ai fini mon travail.");
    }

    @Override
    public void run() {
        travailler();
    }
}