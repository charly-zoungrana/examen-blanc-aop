package net.zoungrana.securite;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Authentification {
    private static final ThreadLocal<Set<String>> ROLES_COURANTS = new ThreadLocal<>();
    private static final SecureRandom ALEA = new SecureRandom();

    private final Map<String, Compte> comptes = new HashMap<>();

    private Authentification() {
    }

    public static Authentification creer() {
        return new Authentification();
    }

    public void ajouterCompte(String nom, char[] motDePasse, Set<String> roles) {
        Objects.requireNonNull(nom, "nom");
        if (nom.isBlank() || motDePasse.length == 0) {
            throw new IllegalArgumentException("Nom et mot de passe requis");
        }
        byte[] sel = new byte[16];
        ALEA.nextBytes(sel);
        Compte compte = new Compte(sel, empreinte(motDePasse, sel), Set.copyOf(roles));
        if (comptes.putIfAbsent(nom, compte) != null) {
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
    }

    public void executer(String nom, char[] motDePasse, Runnable operation) {
        Compte compte = comptes.get(nom);
        if (compte == null || !MessageDigest.isEqual(compte.empreinte(), empreinte(motDePasse, compte.sel()))) {
            throw new SecurityException("Identifiants invalides");
        }

        Set<String> anciensRoles = ROLES_COURANTS.get();
        ROLES_COURANTS.set(compte.roles());
        try {
            operation.run();
        } finally {
            if (anciensRoles == null) {
                ROLES_COURANTS.remove();
            } else {
                ROLES_COURANTS.set(anciensRoles);
            }
        }
    }

    public static boolean possedeUnRole(String[] rolesAutorises) {
        Set<String> roles = ROLES_COURANTS.get();
        if (roles == null) {
            return false;
        }
        for (String role : rolesAutorises) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    private static byte[] empreinte(char[] motDePasse, byte[] sel) {
        PBEKeySpec specification = new PBEKeySpec(motDePasse, sel, 600_000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(specification).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Impossible de vérifier le mot de passe", exception);
        } finally {
            specification.clearPassword();
        }
    }

    private record Compte(byte[] sel, byte[] empreinte, Set<String> roles) {
    }
}
