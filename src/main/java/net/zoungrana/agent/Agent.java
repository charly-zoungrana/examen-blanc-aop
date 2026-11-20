package net.zoungrana.agent;

import net.zoungrana.aspect.Cachable;
import net.zoungrana.aspect.Log;
import net.zoungrana.observateur.EvenementTransaction;
import net.zoungrana.observateur.Observateur;
import net.zoungrana.strategie.StrategieNotification;
import net.zoungrana.strategie.StrategieParDefaut;
import net.zoungrana.transaction.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Agent implements Observateur {
    private final String nom;
    private final List<Transaction> transactions = new ArrayList<>();
    private final Set<Observateur> observateurs = new LinkedHashSet<>();
    private StrategieNotification strategie = new StrategieParDefaut();

    public Agent(String nom) {
        this.nom = Objects.requireNonNull(nom, "nom");
    }

    public String getNom() {
        return nom;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public void souscrire(Observateur observateur) {
        Objects.requireNonNull(observateur, "observateur");
        if (observateur != this) {
            observateurs.add(observateur);
        }
    }

    public void ajouterTransaction(Transaction transaction) {
        transactions.add(Objects.requireNonNull(transaction, "transaction"));
        notifierObservateurs(new EvenementTransaction(nom, transaction));
    }

    private void notifierObservateurs(EvenementTransaction evenement) {
        for (Observateur observateur : List.copyOf(observateurs)) {
            observateur.mettreAJour(evenement);
        }
    }

    @Override
    public void mettreAJour(EvenementTransaction evenement) {
        strategie.traiter(evenement);
    }

    public void changerStrategie(StrategieNotification strategie) {
        this.strategie = Objects.requireNonNull(strategie, "strategie");
    }

    public void afficher() {
        System.out.println(nom);
        transactions.forEach(System.out::println);
    }

    @Cachable
    @Log
    public Transaction transactionMax() {
        return transactions.stream().max(Comparator.comparingDouble(Transaction::getMontant)).orElse(null);
    }
}
