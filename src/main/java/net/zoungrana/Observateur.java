package net.zoungrana;

@FunctionalInterface
public interface Observateur {
    void mettreAJour(EvenementTransaction evenement);
}
