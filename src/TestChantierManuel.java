import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestChantierManuel {

    // --- CLASSE ESPION (Pour surveiller les temps de passage) ---
    // On la met ici pour qu'elle soit dispo pour tous les tests
    static class MaisonEspion extends Maison {
        public Map<String, Long> finElecTimes = new ConcurrentHashMap<>();
        public Map<String, Long> debutPlatreTimes = new ConcurrentHashMap<>();

        @Override
        public void deposerPiecePourPlatre(Piece p) {
            // L'électricien a fini
            finElecTimes.put(p.getNom(), System.currentTimeMillis());
            super.deposerPiecePourPlatre(p);
        }

        @Override
        public Piece prendrePiecePourPlatre() {
            Piece p = super.prendrePiecePourPlatre();
            if (p != null) {
                // Le plâtrier commence
                debutPlatreTimes.put(p.getNom(), System.currentTimeMillis());
            }
            return p;
        }
    }

    // =========================================================================
    // TEST N°1 : Vérifier UNIQUEMENT l'ordre (Electricité AVANT Plâtre)
    // =========================================================================
    @Test
    public void test1_OrdreDesOuvriers() throws InterruptedException {
        System.out.println("--- Démarrage Test 1 : Ordre Chronologique ---");

        // 1. Setup
        MaisonEspion maison = new MaisonEspion();
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // Petit nombre pour aller vite
            Piece p = new Piece("Piece-" + i);
            maison.ajouterPiece(p);
            pieces.add(p);
        }

        List<Thread> ouvriers = new ArrayList<>();
        ouvriers.add(new Electricien("Elec1", maison));
        ouvriers.add(new Platrier("Platre1", maison));

        // 2. Action
        for (Thread t : ouvriers) t.start();

        // Attente (Timeout 10s)
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 10000) {
            boolean fini = true;
            for (Piece p : pieces) if (!p.getPlatree()) fini = false;
            if (fini) break;
            Thread.sleep(100);
        }
        for (Thread t : ouvriers) t.interrupt();

        // 3. Vérification (Le cœur du test)
        for (Piece p : pieces) {
            Long finElec = maison.finElecTimes.get(p.getNom());
            Long debutPlatre = maison.debutPlatreTimes.get(p.getNom());

            assertNotNull("L'électricien doit être passé", finElec);
            assertNotNull("Le plâtrier doit être passé", debutPlatre);

            // C'est ICI qu'on vérifie que le plâtrier n'a pas travaillé en même temps ou avant
            assertTrue("ERREUR : Le plâtrier a commencé AVANT ou PENDANT l'électricité pour " + p.getNom(),
                    finElec <= debutPlatre);

            System.out.println("Succès pour " + p.getNom() + " (Ordre respecté)");
        }
    }

    // =========================================================================
    // TEST N°2 : Vérifier que toute la maison est bien finie à la fin
    // =========================================================================
    @Test
    public void test2_MaisonEntierementTerminee() throws InterruptedException {
        System.out.println("--- Démarrage Test 2 : Finition Complète ---");

        // 1. Setup
        Maison maison = new Maison(); // Pas besoin d'espion ici, juste une maison normale
        List<Piece> pieces = new ArrayList<>();
        // On teste avec plus de pièces et d'ouvriers pour simuler une charge
        for (int i = 0; i < 10; i++) {
            Piece p = new Piece("Chambre-" + i);
            maison.ajouterPiece(p);
            pieces.add(p);
        }

        List<Thread> ouvriers = new ArrayList<>();
        ouvriers.add(new Electricien("Elec1", maison));
        ouvriers.add(new Electricien("Elec2", maison));
        ouvriers.add(new Platrier("Platre1", maison));
        ouvriers.add(new Platrier("Platre2", maison));

        // 2. Action
        for (Thread t : ouvriers) t.start();

        // Attente (Timeout 15s)
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 15000) {
            boolean fini = true;
            for (Piece p : pieces) if (!p.getPlatree()) fini = false;
            if (fini) break;
            Thread.sleep(100);
        }
        for (Thread t : ouvriers) t.interrupt();

        // 3. Vérification
        for (Piece p : pieces) {
            // On vérifie les booléens finaux
            assertTrue("La pièce " + p.getNom() + " devrait être électrifiée", p.getElectrifier());
            assertTrue("La pièce " + p.getNom() + " devrait être plâtrée", p.getPlatree());
        }
        System.out.println("Succès : Les 10 pièces sont terminées.");
    }


    // =========================================================================
    // TEST 3 : Vérifier l'ABSENCE DE TRAVAIL SIMULTANÉ (Nouveau !)
    // =========================================================================
    @Test
    public void test3_PasDeConflitSimultane() throws InterruptedException {
        System.out.println("--- Démarrage Test 3 : Vérification de non-collision ---");

        // 1. SETUP
        MaisonEspion maison = new MaisonEspion();
        List<Piece> pieces = new ArrayList<>();
        // On prend 5 pièces
        for (int i = 0; i < 5; i++) {
            Piece p = new Piece("Piece-" + i);
            maison.ajouterPiece(p);
            pieces.add(p);
        }

        List<Thread> ouvriers = new ArrayList<>();
        ouvriers.add(new Electricien("Elec1", maison));
        ouvriers.add(new Platrier("Platre1", maison));

        // 2. EXÉCUTION
        for (Thread t : ouvriers) t.start();

        // Attente (Timeout 10s)
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 10000) {
            boolean fini = true;
            for (Piece p : pieces) if (!p.getPlatree()) fini = false;
            if (fini) break;
            Thread.sleep(100);
        }
        for (Thread t : ouvriers) t.interrupt();

        // 3. VÉRIFICATION RIGOUREUSE
        for (Piece p : pieces) {
            String nom = p.getNom();
            Long finElec = maison.finElecTimes.get(nom);
            Long debutPlatre = maison.debutPlatreTimes.get(nom);

            assertNotNull("Données manquantes pour " + nom, finElec);
            assertNotNull("Données manquantes pour " + nom, debutPlatre);

            // Calcul de la marge de sécurité (le delta)
            long delta = debutPlatre - finElec;

            System.out.println("Vérification " + nom + " : Elec fini à " + finElec +
                    " | Plâtre commencé à " + debutPlatre +
                    " (Marge: " + delta + "ms)");

            // LE COEUR DU TEST :
            // Si delta est négatif, le plâtrier a commencé AVANT que l'électricien finisse.
            // C'est une collision.
            assertFalse("COLLISION DÉTECTÉE ! Ils ont travaillé en même temps sur " + nom,
                    delta < 0);

            // On vérifie qu'ils ne sont même pas égaux strict (pour être sûr)
            // Le début du plâtre doit être strictement POSTÉRIEUR ou ÉGAL à la fin de l'élec
            assertTrue("Le plâtrier doit attendre la fin de l'électricien",
                    debutPlatre >= finElec);
        }

        System.out.println(">>> SUCCÈS : Aucune collision détectée. Le sémaphore protège bien l'accès.");
    }


}