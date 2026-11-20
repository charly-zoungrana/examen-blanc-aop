package net.zoungrana.strategie;

import net.zoungrana.observateur.EvenementTransaction;
import net.zoungrana.transaction.Transaction;

public final class ScoringStrategy implements StrategieNotification {
    private double solde;

    @Override
    public void traiter(EvenementTransaction evenement) {
        Transaction transaction = evenement.transaction();
        solde += switch (transaction.getType()) {
            case VENTE -> transaction.getMontant();
            case ACHAT -> -transaction.getMontant();
        };
    }

    public double getSolde() {
        return solde;
    }
}
