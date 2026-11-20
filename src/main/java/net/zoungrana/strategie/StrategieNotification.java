package net.zoungrana.strategie;

import net.zoungrana.observateur.EvenementTransaction;

@FunctionalInterface
public interface StrategieNotification {
    void traiter(EvenementTransaction evenement);
}
