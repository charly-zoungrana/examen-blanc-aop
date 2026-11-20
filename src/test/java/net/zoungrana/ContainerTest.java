package net.zoungrana;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {
    private final Container conteneur = Container.getInstance();

    @Test
    void partageUneSeuleInstanceEtGereLesAgentsParNom() {
        Agent agent = new Agent("Test-recherche");
        try {
            conteneur.ajouterAgent(agent);

            assertSame(conteneur, Container.getInstance());
            assertSame(agent, Container.getInstance().chercherAgent(agent.getNom()));

            conteneur.supprimerAgent(agent.getNom());
            assertNull(conteneur.chercherAgent(agent.getNom()));
        } finally {
            conteneur.supprimerAgent(agent.getNom());
        }
    }

    @Test
    void delegueAuPortHdmiEtAUnAfficheurVgaAdapte() {
        Agent agent = new Agent("Test-affichage");
        List<String> affichages = new ArrayList<>();
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        try {
            conteneur.ajouterAgent(agent);
            conteneur.brancher(affichages::add);
            conteneur.afficher();

            String etat = "Agents : [Test-affichage]";
            assertEquals(List.of(etat), affichages);

            conteneur.brancher(new AdaptateurVGA(new AfficheurVGA(new PrintStream(sortie))));
            conteneur.afficher();
            assertEquals(etat, sortie.toString(StandardCharsets.UTF_8).strip());
        } finally {
            conteneur.supprimerAgent(agent.getNom());
            conteneur.brancher(new AfficheurHDMI());
        }
    }
}
