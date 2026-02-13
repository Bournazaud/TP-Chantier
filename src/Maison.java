import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Maison {
    private Queue<Piece> aFaireElec= new LinkedList<>();
    private Queue<Piece> aFairePlatre = new LinkedList<>();

    //Sémaphore
    private Semaphore mutex = new Semaphore(1);
    private Semaphore Piecedispopourplatre = new Semaphore(0);

    public void ajouterPiece(Piece p){
        aFaireElec.add(p);
    }

    //Electricien

    public Piece prendrePiecePourElec(){
        Piece p = null;
        try {
            mutex.acquire();
            p=aFaireElec.poll();
            mutex.release();
        }catch (InterruptedException e) {e.printStackTrace();}
        return  p;
    }

    public void deposerPiecePourPlatre(Piece p){
        try {
            mutex.acquire();
            p.setElectrifier(true);
            aFairePlatre.add(p);
            mutex.release();
            // On fait appel a un platrier
            Piecedispopourplatre.release();
        }catch (InterruptedException e) {e.printStackTrace();}
    }
    //Plâtrier

    public Piece prendrePiecePourPlatre() {
        Piece p = null;
        try {
            Piecedispopourplatre.acquire();
            mutex.acquire();
            p = aFairePlatre.poll();
            mutex.release();
        } catch (InterruptedException e) {e.printStackTrace();}
        return p;
    }
}
