package net.zoungrana.strategie;

import net.zoungrana.observateur.EvenementTransaction;
import net.zoungrana.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public final class HistoryStrategy implements StrategieNotification {
    private final List<Transaction> historique = new ArrayList<>();

    @Override
    public void traiter(EvenementTransaction evenement) {
        historique.add(evenement.transaction());
    }

    public List<Transaction> getHistorique() {
        return List.copyOf(historique);
    }
}
