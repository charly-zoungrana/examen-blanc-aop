package net.zoungrana.aspect;

import net.zoungrana.securite.Authentification;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;

@Aspect
@DeclarePrecedence("net.zoungrana.aspect.SecuriteAspect, *")
public class SecuriteAspect {
    @Before(value = "execution(* *(..)) && @annotation(regle)", argNames = "regle")
    public void verifierDroits(SecuredBy regle) {
        if (!Authentification.possedeUnRole(regle.roles())) {
            throw new SecurityException("Accès refusé");
        }
    }
}
