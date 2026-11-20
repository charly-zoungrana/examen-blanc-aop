package net.zoungrana;

import java.io.Console;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Console console = System.console();
        String nomConfigure = System.getenv("EXAMEN_USERNAME");
        String motDePasseConfigure = System.getenv("EXAMEN_PASSWORD");
        if (console == null || nomConfigure == null || motDePasseConfigure == null) {
            System.err.println("Lancez le programme depuis un terminal avec EXAMEN_USERNAME et EXAMEN_PASSWORD.");
            return;
        }

        Authentification authentification = new Authentification();
        char[] secret = motDePasseConfigure.toCharArray();
        try {
            authentification.ajouterCompte(nomConfigure, secret, Set.of("ADMIN"));
        } finally {
            Arrays.fill(secret, '\0');
        }

        String nom = console.readLine("Nom utilisateur : ");
        char[] motDePasse = console.readPassword("Mot de passe : ");
        if (nom == null || motDePasse == null) {
            return;
        }
        try {
            authentification.executer(nom, motDePasse, Main::demonstration);
        } catch (SecurityException exception) {
            System.err.println(exception.getMessage());
        } finally {
            Arrays.fill(motDePasse, '\0');
        }
    }

    private static void demonstration() {
        Container conteneur = Container.getInstance();
        Agent vendeur = new Agent("Vendeur");
        Agent observateur = new Agent("Observateur");
        ScoringStrategy score = new ScoringStrategy();
        observateur.changerStrategie(score);
        vendeur.souscrire(observateur);

        conteneur.ajouterAgent(vendeur);
        conteneur.ajouterAgent(observateur);
        conteneur.brancher(new AdaptateurVGA(new AfficheurVGA()));
        conteneur.afficher();

        vendeur.ajouterTransaction(new TransactionBuilder().id("V1").date(LocalDate.now())
                .montant(125).type(TypeTransaction.VENTE).build());
        vendeur.afficher();
        System.out.println("Montant maximum : " + vendeur.transactionMax().getMontant());
        System.out.println("Solde observé : " + score.getSolde());
    }
}
