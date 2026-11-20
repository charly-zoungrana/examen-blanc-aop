package net.zoungrana;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;

@Aspect
@DeclarePrecedence("net.zoungrana.SecuriteAspect, *")
public class SecuriteAspect {
    @Before(value = "execution(* *(..)) && @annotation(regle)", argNames = "regle")
    public void verifierDroits(SecuredBy regle) {
        if (!Authentification.possedeUnRole(regle.roles())) {
            throw new SecurityException("Accès refusé");
        }
    }
}
