package net.zoungrana;

import java.util.Objects;

public record EvenementTransaction(String nomAgent, Transaction transaction) {
    public EvenementTransaction {
        Objects.requireNonNull(nomAgent, "nomAgent");
        Objects.requireNonNull(transaction, "transaction");
    }
}
