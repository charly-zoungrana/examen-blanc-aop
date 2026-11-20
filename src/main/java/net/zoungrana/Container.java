package net.zoungrana;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Container {
    private static final Container instance = new Container();

    private final HashMap<String, Agent> agents = new HashMap<>();
    private HDMI afficheur;

    private Container() {
    }

    public static Container getInstance() {
        return instance;
    }

    public void ajouterAgent(Agent agent) {
        Objects.requireNonNull(agent, "agent");
        agents.put(agent.getNom(), agent);
    }

    public void supprimerAgent(String nom) {
        agents.remove(nom);
    }

    public Agent chercherAgent(String nom) {
        return agents.get(nom);
    }

    public void brancher(HDMI afficheur) {
        this.afficheur = Objects.requireNonNull(afficheur, "afficheur");
    }

    public void afficher() {
        if (afficheur != null) {
            String etat = agents.keySet().stream().sorted()
                    .collect(Collectors.joining(", ", "Agents : [", "]"));
            afficheur.afficher(etat);
        }
    }
}
