package net.zoungrana;

public final class StrategieParDefaut implements StrategieNotification {
    @Override
    public void traiter(EvenementTransaction evenement) {
        System.out.printf("Transaction reçue de %s : %s%n", evenement.nomAgent(), evenement.transaction());
    }
}
