package net.zoungrana.affichage;

import java.io.PrintStream;
import java.util.Objects;

public final class AfficheurVGA {
    private final PrintStream sortie;

    public AfficheurVGA() {
        this(System.out);
    }

    public AfficheurVGA(PrintStream sortie) {
        this.sortie = Objects.requireNonNull(sortie, "sortie");
    }

    public void afficherVGA(String etat) {
        sortie.println(etat);
    }
}
