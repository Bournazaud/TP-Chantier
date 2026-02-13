public class Piece {
    private String Nom;
    private boolean electrifier = false;
    private boolean platree = false;

    public Piece(String nom){
        this.Nom = nom;

    }
    public String getNom () {return Nom;}
}
