package net.zoungrana.observateur;

@FunctionalInterface
public interface Observateur {
    void mettreAJour(EvenementTransaction evenement);
}
