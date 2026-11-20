package net.zoungrana.affichage;

public final class AfficheurHDMI implements HDMI {
    @Override
    public void afficher(String etat) {
        System.out.println(etat);
    }
}
