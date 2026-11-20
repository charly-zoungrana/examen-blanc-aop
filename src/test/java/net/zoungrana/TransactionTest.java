package net.zoungrana;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {
    private static final LocalDate DATE = LocalDate.of(2026, 11, 20);

    @Test
    void construitUneTransactionAvecLesQuatreChamps() {
        Transaction transaction = builder().build();

        assertEquals("T1", transaction.getId());
        assertEquals(DATE, transaction.getDate());
        assertEquals(125.5, transaction.getMontant());
        assertEquals(TypeTransaction.VENTE, transaction.getType());
    }

    @Test
    void exigeExplicitementUnMontant() {
        TransactionBuilder incomplete = new TransactionBuilder()
                .id("T1").date(DATE).type(TypeTransaction.VENTE);

        assertThrows(IllegalStateException.class, incomplete::build);
        assertEquals(0.0, incomplete.montant(0.0).build().getMontant());
    }

    @Test
    void rejetteLesMontantsInvalides() {
        assertThrows(IllegalArgumentException.class, () -> builder().montant(-1).build());
        assertThrows(IllegalArgumentException.class, () -> builder().montant(Double.NaN).build());
        assertThrows(IllegalArgumentException.class, () -> builder().montant(Double.POSITIVE_INFINITY).build());
    }

    private TransactionBuilder builder() {
        return new TransactionBuilder().id("T1").date(DATE).montant(125.5).type(TypeTransaction.VENTE);
    }
}
