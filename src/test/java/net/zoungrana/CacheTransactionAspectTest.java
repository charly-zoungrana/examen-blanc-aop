package net.zoungrana;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class CacheTransactionAspectTest {
    @Test
    void reutiliseLeMaximumEtInvalideLeCacheApresAjout() {
        Logger logger = Logger.getLogger(JournalisationAspect.class.getName());
        AtomicInteger executions = new AtomicInteger();
        Handler collecteur = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getMessage().contains("transactionMax")) {
                    executions.incrementAndGet();
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        logger.addHandler(collecteur);
        try {
            Agent agent = new Agent("Agent cache");
            Agent autre = new Agent("Autre agent");
            assertNull(agent.transactionMax());
            assertNull(agent.transactionMax());
            assertEquals(1, executions.get());

            Transaction vente = new TransactionBuilder().id("V").date(LocalDate.of(2026, 11, 20))
                    .montant(120).type(TypeTransaction.VENTE).build();
            agent.souscrire(evenement -> assertSame(vente, agent.transactionMax()));
            agent.ajouterTransaction(vente);

            assertSame(vente, agent.transactionMax());
            assertNull(autre.transactionMax());
            assertEquals(3, executions.get());
        } finally {
            logger.removeHandler(collecteur);
        }
    }
}
