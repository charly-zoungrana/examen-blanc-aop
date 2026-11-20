package net.zoungrana.affichage;

import java.util.Objects;

public final class AdaptateurVGA implements HDMI {
    private final AfficheurVGA afficheur;

    public AdaptateurVGA(AfficheurVGA afficheur) {
        this.afficheur = Objects.requireNonNull(afficheur, "afficheur");
    }

    @Override
    public void afficher(String etat) {
        afficheur.afficherVGA(etat);
    }
}
