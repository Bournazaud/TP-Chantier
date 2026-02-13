public class Platrier extends Thread {

    public Platrier(String nom) {
        super(nom);
    }

    public void travailler() {
        System.out.println(getName() + " (Plâtrier) : Je commence à travailler.");
        try {
            Thread.sleep(10000); // 10 secondes de pause
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " (Plâtrier) : J'ai fini mon travail.");
    }

    @Override
    public void run() {
        travailler();
    }
}