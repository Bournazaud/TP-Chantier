import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
       /*
        // Liste pour stocker tous nos ouvriers
        List<Thread> equipe = new ArrayList<>();

        // 1. Création des 3 électriciens
        equipe.add(new Electricien("Michel", maison));
        equipe.add(new Electricien("Thomas", maison));
        equipe.add(new Electricien("Luc", maison));

        // 2. Création des 2 plâtriers
        equipe.add(new Platrier("Bernard", maison));
        equipe.add(new Platrier("Philippe", maison));

        System.out.println("--- DÉBUT DU CHANTIER (PARTIE 1) ---");

        // 3. Lancement du travail (Start)
        for (Thread ouvrier : equipe) {
            ouvrier.start();
        }

        // 4. Attente de la fin (Join)
        for (Thread ouvrier : equipe) {
            try {
                ouvrier.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 5. Message final
        System.out.println(">>> Tous les ouvriers ont fini leur travail !");*/

        // 1. Création du chantier (Maison)
        Maison maison = new Maison();

        // Liste pour garder une référence sur les pièces pour vérifier à la fin
        List<Piece> verificationPieces = new ArrayList<>();

        // 2. Création des piece de la maison
        String[] noms = {"Cuisine", "Salon", "Chambre 1", "Chambre 2", "SDB", "WC", "Entrée", "Garage", "Bureau", "Grenier"};
        for (String nom : noms) {
            Piece p = new Piece(nom);
            maison.ajouterPiece(p);
            verificationPieces.add(p);
        }

        // 3. Création des ouvriers en leur donnant la maison
        List<Thread> electriciens = new ArrayList<>();
        List<Thread> platriers = new ArrayList<>();

        electriciens.add(new Electricien("Michel", maison));
        electriciens.add(new Electricien("Thomas", maison));
        electriciens.add(new Electricien("Luc", maison));

        platriers.add(new Platrier("Bernard", maison));
        platriers.add(new Platrier("Philippe", maison));

        System.out.println("--- DÉBUT DU CHANTIER (PARTIE 2) ---");

        // 4. Lancement des threads
        for (Thread t : platriers) t.start(); // Les plâtriers commencent (et attendent)
        for (Thread t : electriciens) t.start(); // Les électriciens commencent

        // 5. Attente de la fin des électriciens (Join)
        for (Thread t : electriciens) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(">>> Tous les électriciens ont fini !");

        // 6. Attente de la fin du chantier (Plâtriers)
        // Les plâtriers sont en boucle infinie (while !interrupted), donc on ne peut pas faire join() tout de suite.
        // On attend que toutes les pièces soient marquées "platree".
        while (true) {
            boolean toutEstFini = true;
            for (Piece p : verificationPieces) {
                if (!p.getPlatree()) {
                    toutEstFini = false;
                    break;
                }
            }
            if (toutEstFini) break;

            try { Thread.sleep(500); } catch (Exception e) {}
        }

        // Une fois tout fini, on arrête les plâtriers qui attendent peut-être encore
        for (Thread t : platriers) t.interrupt();

        System.out.println(">>> LA MAISON EST TERMINÉE !");



    }

}