package net.zoungrana;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentTest {
    @Test
    void notifieSeulementLesAbonnesSansDoublon() {
        Agent source = new Agent("Source");
        List<EvenementTransaction> recus = new ArrayList<>();
        Observateur observateur = recus::add;
        source.souscrire(observateur);
        source.souscrire(observateur);
        Transaction transaction = transaction("A", 50, TypeTransaction.VENTE);

        source.ajouterTransaction(transaction);

        assertEquals(List.of(new EvenementTransaction("Source", transaction)), recus);
    }

    @Test
    void changeDeStrategiePendantExecution() {
        Agent source = new Agent("Source");
        Agent abonne = new Agent("Abonné");
        source.souscrire(abonne);
        ScoringStrategy score = new ScoringStrategy();
        abonne.changerStrategie(score);
        source.ajouterTransaction(transaction("V", 70, TypeTransaction.VENTE));
        source.ajouterTransaction(transaction("A", 20, TypeTransaction.ACHAT));

        HistoryStrategy historique = new HistoryStrategy();
        abonne.changerStrategie(historique);
        Transaction derniere = transaction("H", 100, TypeTransaction.VENTE);
        source.ajouterTransaction(derniere);

        assertEquals(50, score.getSolde());
        assertEquals(List.of(derniere), historique.getHistorique());
        assertTrue(abonne.getTransactions().isEmpty());
    }

    @Test
    void retourneLaTransactionAuMontantMaximal() {
        Agent agent = new Agent("Source");
        assertNull(agent.transactionMax());
        Transaction maximum = transaction("MAX", 100, TypeTransaction.ACHAT);
        agent.ajouterTransaction(maximum);
        agent.ajouterTransaction(transaction("MIN", 20, TypeTransaction.VENTE));

        assertSame(maximum, agent.transactionMax());
    }

    private Transaction transaction(String id, double montant, TypeTransaction type) {
        return new TransactionBuilder().id(id).date(LocalDate.of(2026, 11, 20))
                .montant(montant).type(type).build();
    }
}
