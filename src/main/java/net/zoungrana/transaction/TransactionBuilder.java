package net.zoungrana.transaction;

import java.time.LocalDate;

public final class TransactionBuilder {
    private String id;
    private LocalDate date;
    private Double montant;
    private TypeTransaction type;

    public TransactionBuilder id(String id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public TransactionBuilder montant(double montant) {
        this.montant = montant;
        return this;
    }

    public TransactionBuilder type(TypeTransaction type) {
        this.type = type;
        return this;
    }

    public Transaction build() {
        if (id == null || date == null || montant == null || type == null) {
            throw new IllegalStateException("Tous les champs de la transaction sont requis");
        }
        return new Transaction(id, date, montant, type);
    }
}
