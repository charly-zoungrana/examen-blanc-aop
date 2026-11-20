package net.zoungrana;

import java.time.LocalDate;
import java.util.Objects;

public final class Transaction {
    private final String id;
    private final LocalDate date;
    private final double montant;
    private final TypeTransaction type;

    Transaction(String id, LocalDate date, double montant, TypeTransaction type) {
        this.id = Objects.requireNonNull(id, "id");
        this.date = Objects.requireNonNull(date, "date");
        this.type = Objects.requireNonNull(type, "type");
        if (!Double.isFinite(montant) || montant < 0) {
            throw new IllegalArgumentException("Le montant doit être fini et positif ou nul");
        }
        this.montant = montant;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getMontant() {
        return montant;
    }

    public TypeTransaction getType() {
        return type;
    }

    @Override
    public String toString() {
        return "%s | %s | %s | %s".formatted(id, date, type, montant);
    }
}
