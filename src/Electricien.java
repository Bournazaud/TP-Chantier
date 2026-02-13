public class Electricien extends Thread {
    private Maison maison;

    public Electricien(String nom, Maison maison) {
        super(nom);
        this.maison = maison;
    }

    //Partie 1
    /*public void travailler() {
        System.out.println(getName() + " (Electricien) : Je commence à travailler.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " (Electricien) : J'ai fini mon travail.");
    }*/

    @Override
    public void run() {
        while (true){
            // L'electricien est en recherche de travail
            Piece p = maison.prendrePiecePourElec();
            if (p == null) break;
            // On travaille
            System.out.println(getName() + " (Electricien) commence : " + p.getNom());
            try {
                Thread.sleep(5000); // On fait attendre pour simuler le travail
            }catch (InterruptedException e){e.printStackTrace();}
            System.out.println(getName() + " (Electricien) a fini : " + p.getNom());

            maison.deposerPiecePourPlatre(p);
            }
    }
}