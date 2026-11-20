package net.zoungrana;

import net.zoungrana.affichage.AdaptateurVGA;
import net.zoungrana.affichage.AfficheurHDMI;
import net.zoungrana.affichage.AfficheurVGA;
import net.zoungrana.agent.Agent;
import net.zoungrana.aspect.JournalisationAspect;
import net.zoungrana.conteneur.Container;
import net.zoungrana.securite.Authentification;
import net.zoungrana.strategie.HistoryStrategy;
import net.zoungrana.strategie.ScoringStrategy;
import net.zoungrana.transaction.Transaction;
import net.zoungrana.transaction.TransactionBuilder;
import net.zoungrana.transaction.TypeTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static net.zoungrana.transaction.TypeTransaction.ACHAT;
import static net.zoungrana.transaction.TypeTransaction.VENTE;

public final class Main {
    private static final LocalDate DATE_EXEMPLE = LocalDate.of(2026, 11, 20);
    private static final String ADMIN = "admin-demo";
    private static final String LECTEUR = "lecteur-demo";
    private static final String SECRET_DEMO = "mot-de-passe-demo";

    private Main() {
    }

    public static void main(String[] args) {
        Map<String, Runnable> scenarios = Map.of(
                "transaction", Main::scenarioTransaction,
                "agent", Main::scenarioAgent,
                "conteneur", Main::scenarioConteneur,
                "aspects", Main::scenarioAspects
        );

        if (args.length == 0 || "tout".equals(args[0])) {
            for (String nom : List.of("transaction", "agent", "conteneur", "aspects")) {
                scenarios.get(nom).run();
            }
            System.out.println("Main : les quatre scénarios ont réussi.");
            return;
        }

        Runnable scenario = scenarios.get(args[0]);
        if (scenario == null) {
            throw new IllegalArgumentException("Scénario inconnu : " + args[0]);
        }
        scenario.run();
    }

    private static void scenarioTransaction() {
        titre("01  Transaction et Builder");
        Transaction vente = transaction("V-001", 125, VENTE);
        Transaction achat = transaction("A-001", 40, ACHAT);

        System.out.println("Vente créée : " + vente);
        System.out.println("Achat créé  : " + achat);
        verifier(vente.getId().equals("V-001") && vente.getDate().equals(DATE_EXEMPLE)
                && vente.getMontant() == 125 && vente.getType() == VENTE, "Les quatre champs sont renseignés");
        refusAttendu(IllegalStateException.class, "Construction sans montant",
                () -> new TransactionBuilder().id("INCOMPLETE").date(DATE_EXEMPLE).type(ACHAT).build());
        refusAttendu(IllegalArgumentException.class, "Construction avec un montant négatif",
                () -> transaction("INVALIDE", -1, ACHAT));
        System.out.println("Scénario Transaction réussi.");
    }

    private static void scenarioAgent() {
        titre("02  Agent, Observer et Strategy");
        Agent source = new Agent("Alice");
        Agent abonne = new Agent("Bob");
        source.souscrire(abonne);

        Transaction initiale = transaction("V-101", 100, VENTE);
        source.ajouterTransaction(initiale);

        ScoringStrategy score = new ScoringStrategy();
        abonne.changerStrategie(score);
        source.ajouterTransaction(transaction("V-102", 80, VENTE));
        source.ajouterTransaction(transaction("A-101", 30, ACHAT));
        System.out.printf("Bob passe au scoring : 80 - 30 = %.2f%n", score.getSolde());
        verifier(score.getSolde() == 50, "Solde de Bob : 50");

        HistoryStrategy historique = new HistoryStrategy();
        abonne.changerStrategie(historique);
        Transaction derniere = transaction("V-103", 150, VENTE);
        source.ajouterTransaction(derniere);
        System.out.println("Bob passe à l’historique : " + historique.getHistorique());
        verifier(historique.getHistorique().equals(List.of(derniere)), "La nouvelle notification est archivée");
        verifier(abonne.getTransactions().isEmpty(), "Bob ne reçoit pas la transaction dans son registre propre");

        System.out.println("Registre de l’émetteur :");
        source.afficher();
        Transaction maximum = source.transactionMax();
        System.out.println("Transaction maximale : " + maximum);
        verifier(maximum == derniere, "Maximum attendu : 150");
        System.out.println("Scénario Agent réussi.");
    }

    private static void scenarioConteneur() {
        titre("03  Conteneur, HDMI et adaptateur VGA");
        authentification().executer(ADMIN, SECRET_DEMO.toCharArray(), () -> {
            Container conteneur = Container.getInstance();
            Agent nord = new Agent("Agence Nord");
            Agent sud = new Agent("Agence Sud");

            verifier(conteneur == Container.getInstance(), "Les deux accès renvoient le même conteneur");
            conteneur.ajouterAgent(nord);
            conteneur.ajouterAgent(sud);
            Agent resultat = conteneur.chercherAgent(sud.getNom());
            System.out.println("Recherche « Agence Sud » : " + resultat.getNom());
            verifier(resultat == sud, "La recherche retrouve l’agent par son nom");

            conteneur.brancher(new AfficheurHDMI());
            System.out.print("État via HDMI : ");
            conteneur.afficher();

            conteneur.supprimerAgent(nord.getNom());
            Agent supprime = conteneur.chercherAgent(nord.getNom());
            System.out.println("Recherche après suppression d’« Agence Nord » : " + supprime);
            verifier(supprime == null, "L’agent supprimé est absent");

            conteneur.brancher(new AdaptateurVGA(new AfficheurVGA()));
            System.out.print("État via l’adaptateur VGA : ");
            conteneur.afficher();
            conteneur.supprimerAgent(sud.getNom());
        });
        System.out.println("Scénario Conteneur réussi.");
    }

    private static void scenarioAspects() {
        titre("04  Journalisation, cache et sécurité");
        demontrerJournalisationEtCache();

        Container conteneur = Container.getInstance();
        Authentification auth = authentification();
        refusAttendu(SecurityException.class, "Consultation sans connexion", conteneur::afficher);
        refusAttendu(SecurityException.class, "Connexion avec un mauvais mot de passe",
                () -> auth.executer(ADMIN, "incorrect".toCharArray(), conteneur::afficher));

        auth.executer(LECTEUR, SECRET_DEMO.toCharArray(), () -> {
            conteneur.chercherAgent("Agence Centre");
            System.out.println("LECTEUR peut consulter le conteneur.");
            refusAttendu(SecurityException.class, "Ajout par LECTEUR",
                    () -> conteneur.ajouterAgent(new Agent("Agence Centre")));
        });

        auth.executer(ADMIN, SECRET_DEMO.toCharArray(), () -> {
            Agent agent = new Agent("Agence Centre");
            conteneur.ajouterAgent(agent);
            verifier(conteneur.chercherAgent(agent.getNom()) == agent, "ADMIN peut ajouter et retrouver l’agent");
            conteneur.supprimerAgent(agent.getNom());
        });
        System.out.println("Scénario Aspects réussi.");
    }

    private static void demontrerJournalisationEtCache() {
        Logger journal = Logger.getLogger(JournalisationAspect.class.getName());
        boolean relaisActif = journal.getUseParentHandlers();
        class Collecteur extends Handler {
            private int executions;

            @Override
            public void publish(LogRecord entree) {
                executions++;
                System.out.println("  @Log : " + entree.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        }

        Collecteur traces = new Collecteur();
        journal.setUseParentHandlers(false);
        journal.addHandler(traces);
        try {
            Agent agent = new Agent("Max");
            Transaction premiere = transaction("V-201", 75, VENTE);
            agent.ajouterTransaction(premiere);

            Transaction maximum = agent.transactionMax();
            System.out.println("Premier appel : maximum = " + maximum.getMontant());
            verifier(maximum == premiere && traces.executions == 1, "Un calcul chronométré");

            Transaction suivant = agent.transactionMax();
            System.out.println("Deuxième appel : maximum = " + suivant.getMontant());
            verifier(suivant == premiere && traces.executions == 1, "Résultat en cache, aucun nouveau calcul");

            Transaction nouvelle = transaction("V-202", 180, VENTE);
            agent.ajouterTransaction(nouvelle);
            Transaction actualise = agent.transactionMax();
            System.out.println("Après ajout : maximum = " + actualise.getMontant());
            verifier(actualise == nouvelle && traces.executions == 2, "Cache invalidé et maximum recalculé");
        } finally {
            journal.removeHandler(traces);
            journal.setUseParentHandlers(relaisActif);
        }
    }

    private static Authentification authentification() {
        Authentification comptes = Authentification.creer();
        comptes.ajouterCompte(ADMIN, SECRET_DEMO.toCharArray(), Set.of("ADMIN"));
        comptes.ajouterCompte(LECTEUR, SECRET_DEMO.toCharArray(), Set.of("LECTEUR"));
        return comptes;
    }

    private static Transaction transaction(String id, double montant, TypeTransaction type) {
        return new TransactionBuilder().id(id).date(DATE_EXEMPLE).montant(montant).type(type).build();
    }

    private static void titre(String libelle) {
        System.out.println();
        System.out.println("=== " + libelle + " ===");
    }

    private static void verifier(boolean condition, String libelle) {
        if (!condition) {
            throw new IllegalStateException("Échec du scénario : " + libelle);
        }
        System.out.println("OK - " + libelle);
    }

    private static void refusAttendu(Class<? extends RuntimeException> type, String libelle, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (!type.isInstance(exception)) {
                throw exception;
            }
            System.out.println("OK - " + libelle + " : " + exception.getMessage());
            return;
        }
        throw new IllegalStateException("Échec du scénario : " + libelle + " aurait dû être refusé");
    }
}
