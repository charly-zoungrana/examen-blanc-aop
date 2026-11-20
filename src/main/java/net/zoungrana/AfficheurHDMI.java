package net.zoungrana;

public final class AfficheurHDMI implements HDMI {
    @Override
    public void afficher(String etat) {
        System.out.println(etat);
    }
}
