public class Platrier extends Thread {
    private Maison maison;

    public Platrier(String nom, Maison maison) {
        super(nom);
        this.maison = maison;
    }

    /*public void travailler() {
        System.out.println(getName() + " (Plâtrier) : Je commence à travailler.");
        try {
            Thread.sleep(10000); // 10 secondes de pause
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " (Plâtrier) : J'ai fini mon travail.");
    }*/

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                // 1. Attendre et prendre une pièce (bloquant ici si vide)
                Piece p = maison.prendrePiecePourPlatre();

                // Sécurité si on arrête le thread pendant l'attente
                if (p == null) break;

                // Test de sécurité
                if (!p.getElectrifier()) {
                    System.err.println("ERREUR GRAVE : " + getName() + " travaille sur une pièce non électrifiée !");
                }

                // 2. Travailler
                System.out.println(getName() + " (Plâtrier) commence : " + p.getNom());
                Thread.sleep(2000); // Simulation du travail du platrier

                p.setPlatree(true);
                System.out.println(getName() + " (Plâtrier) a fini : " + p.getNom());
            }
        } catch (InterruptedException e) {}
    }
}