package net.zoungrana;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class JournalisationAspectTest {
    @Test
    void journaliseLaDureeMemeSiLaMethodeEchoue() {
        Logger logger = Logger.getLogger(JournalisationAspect.class.getName());
        List<String> messages = new ArrayList<>();
        Handler collecteur = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
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
            assertThrows(IllegalStateException.class, new Operation()::echouer);
            assertEquals(1, messages.size());
            assertTrue(messages.getFirst().matches(".*echouer.* exécutée en [0-9]+ ns"));
        } finally {
            logger.removeHandler(collecteur);
        }
    }

    static class Operation {
        @Log
        void echouer() {
            throw new IllegalStateException("échec de test");
        }
    }
}
