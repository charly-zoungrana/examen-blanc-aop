package net.zoungrana;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SecuriteAspectTest {
    @Test
    void refuseLesAppelsSansSessionOuAvecUnRoleInsuffisant() {
        Authentification authentification = new Authentification();
        char[] secret = "test-lecteur".toCharArray();
        authentification.ajouterCompte("lecteur", secret, Set.of("LECTEUR"));
        Container container = Container.getInstance();

        assertThrows(SecurityException.class, container::afficher);
        assertThrows(SecurityException.class,
                () -> authentification.executer("lecteur", "incorrect".toCharArray(), container::afficher));

        authentification.executer("lecteur", secret, () -> {
            assertDoesNotThrow(() -> container.chercherAgent("Non inscrit"));
            assertThrows(SecurityException.class, () -> container.ajouterAgent(new Agent("Interdit")));
        });

        assertThrows(SecurityException.class, () -> container.chercherAgent("Non inscrit"));
    }
}
