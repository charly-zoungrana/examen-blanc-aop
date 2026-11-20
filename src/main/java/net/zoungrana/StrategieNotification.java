package net.zoungrana;

@FunctionalInterface
public interface StrategieNotification {
    void traiter(EvenementTransaction evenement);
}
