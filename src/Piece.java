public class Piece {
    private String Nom;
    private boolean electrifier = false;
    private boolean platree = false;

    public Piece(String nom){
        this.Nom = nom;

    }
    public String getNom () {return Nom;}
    public void setElectrifier(boolean etat) {this.electrifier= etat;}
    public void setPlatree(boolean etat) {this.platree= etat;}
    public boolean getElectrifier () {return electrifier;}
    public boolean getPlatree () {return platree;}

}
