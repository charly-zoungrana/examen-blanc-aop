package net.zoungrana;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.logging.Logger;

@Aspect
public class JournalisationAspect {
    private static final Logger LOGGER = Logger.getLogger(JournalisationAspect.class.getName());

    @Around("execution(@net.zoungrana.Log * *(..))")
    public Object mesurer(ProceedingJoinPoint appel) throws Throwable {
        long debut = System.nanoTime();
        try {
            return appel.proceed();
        } finally {
            long duree = System.nanoTime() - debut;
            LOGGER.info(() -> appel.getSignature().toShortString() + " exécutée en " + duree + " ns");
        }
    }
}
