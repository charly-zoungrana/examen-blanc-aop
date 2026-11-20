package net.zoungrana;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Aspect
@DeclarePrecedence("net.zoungrana.CacheTransactionAspect, net.zoungrana.JournalisationAspect")
public class CacheTransactionAspect {
    private final Map<Agent, Optional<Transaction>> maximums =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Around(value = "execution(@net.zoungrana.Cachable net.zoungrana.Transaction net.zoungrana.Agent.transactionMax()) && this(agent)",
            argNames = "appel,agent")
    public Object lireMaximum(ProceedingJoinPoint appel, Agent agent) throws Throwable {
        Optional<Transaction> resultat = maximums.get(agent);
        if (resultat == null) {
            resultat = Optional.ofNullable((Transaction) appel.proceed());
            maximums.put(agent, resultat);
        }
        return resultat.orElse(null);
    }

    @Around(value = "execution(void net.zoungrana.Agent.ajouterTransaction(net.zoungrana.Transaction)) && this(agent)",
            argNames = "appel,agent")
    public Object invaliderMaximum(ProceedingJoinPoint appel, Agent agent) throws Throwable {
        maximums.remove(agent);
        return appel.proceed();
    }
}
