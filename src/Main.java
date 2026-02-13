import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Liste pour stocker tous nos ouvriers
        List<Thread> equipe = new ArrayList<>();

        // 1. Création des 3 électriciens
        equipe.add(new Electricien("Michel"));
        equipe.add(new Electricien("Thomas"));
        equipe.add(new Electricien("Luc"));

        // 2. Création des 2 plâtriers
        equipe.add(new Platrier("Bernard"));
        equipe.add(new Platrier("Philippe"));

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
        System.out.println(">>> Tous les ouvriers ont fini leur travail !");
    }
}