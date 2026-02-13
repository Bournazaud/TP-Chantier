import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChantierTest {

    // Cette classe "Espion" nous permet d'enregistrer quand les choses se passent
    // sans modifier ton code d'origine.
    class MaisonEspion extends Maison {
        // On enregistre l'heure de fin de l'électricité et de début du plâtre pour chaque pièce
        public Map<String, Long> finElecTimes = new ConcurrentHashMap<>();
        public Map<String, Long> debutPlatreTimes = new ConcurrentHashMap<>();

        @Override
        public void deposerPiecePourPlatre(Piece p) {
            // L'électricien dépose la pièce = IL A FINI
            finElecTimes.put(p.getNom(), System.currentTimeMillis());
            super.deposerPiecePourPlatre(p);
        }

        @Override
        public Piece prendrePiecePourPlatre() {
            Piece p = super.prendrePiecePourPlatre();
            if (p != null) {
                // Le plâtrier prend la pièce = IL COMMENCE
                debutPlatreTimes.put(p.getNom(), System.currentTimeMillis());
            }
            return p;
        }
    }

    @Test
    public void testVerificationCompleteDuChantier() throws InterruptedException {
        // 1. SETUP
        MaisonEspion maison = new MaisonEspion();
        List<Piece> piecesRef = new ArrayList<>();

        // On réduit le nombre de pièces pour que le test soit plus rapide
        int nbPieces = 5;
        for (int i = 0; i < nbPieces; i++) {
            Piece p = new Piece("Piece-" + i);
            maison.ajouterPiece(p);
            piecesRef.add(p);
        }

        List<Thread> ouvriers = new ArrayList<>();
        // On crée les ouvriers (note qu'ils utilisent MaisonEspion, mais ils ne le savent pas)
        ouvriers.add(new Electricien("Elec-Test", maison));
        ouvriers.add(new Platrier("Platre-Test", maison));

        // 2. EXÉCUTION
        for (Thread t : ouvriers) t.start();

        // 3. ATTENTE INTELLIGENTE (TIMEOUT)
        // On attend max 30 secondes que tout le monde ait fini
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < 30000) {
            boolean toutFini = true;
            for(Piece p : piecesRef) {
                if(!p.getPlatree()) toutFini = false;
            }
            if(toutFini) break;
            Thread.sleep(100);
        }

        // On arrête les ouvriers proprement
        for (Thread t : ouvriers) t.interrupt();


        // === VÉRIFICATION DES 3 CONDITIONS DEMANDÉES ===

        for (Piece p : piecesRef) {
            String nom = p.getNom();
            Long finElec = maison.finElecTimes.get(nom);
            Long debutPlatre = maison.debutPlatreTimes.get(nom);

            // Sécurité : Vérifier qu'on a bien les logs
            assertNotNull("L'électricien aurait dû passer dans " + nom, finElec);
            assertNotNull("Le plâtrier aurait dû passer dans " + nom, debutPlatre);

            // -------------------------------------------------------------
            // TEST 1 & 2 : L'ORDRE et L'ABSENCE DE TRAVAIL SIMULTANÉ
            // -------------------------------------------------------------
            // Si (Fin élec) < (Début Plâtre), alors :
            // 1. L'électricien a fini AVANT que le plâtrier ne commence (Ordre respecté).
            // 2. Ils n'ont pas travaillé en même temps (Pas de chevauchement).

            assertTrue("ERREUR TEMP: Le plâtrier a commencé avant que l'élec ait fini pour " + nom,
                    finElec <= debutPlatre);

            System.out.println("OK pour " + nom + " : Elec fini à " + finElec + ", Plâtre commencé à " + debutPlatre);


            // -------------------------------------------------------------
            // TEST 3 : MAISON BIEN TERMINÉE
            // -------------------------------------------------------------
            assertTrue("ERREUR FINITION : La pièce " + nom + " devrait être électrifiée", p.getElectrifier());
            assertTrue("ERREUR FINITION : La pièce " + nom + " devrait être plâtrée", p.getPlatree());
        }

        System.out.println(">>> TOUS LES TESTS SONT VALIDÉS !");
    }
}