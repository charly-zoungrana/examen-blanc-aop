package net.zoungrana.aspect;

import net.zoungrana.agent.Agent;
import net.zoungrana.transaction.Transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Aspect
@DeclarePrecedence("net.zoungrana.aspect.CacheTransactionAspect, net.zoungrana.aspect.JournalisationAspect")
public class CacheTransactionAspect {
    private final Map<Agent, Optional<Transaction>> maximums =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Around(value = "execution(@net.zoungrana.aspect.Cachable net.zoungrana.transaction.Transaction net.zoungrana.agent.Agent.transactionMax()) && this(agent)",
            argNames = "appel,agent")
    public Object lireMaximum(ProceedingJoinPoint appel, Agent agent) throws Throwable {
        Optional<Transaction> resultat = maximums.get(agent);
        if (resultat == null) {
            resultat = Optional.ofNullable((Transaction) appel.proceed());
            maximums.put(agent, resultat);
        }
        return resultat.orElse(null);
    }

    @Around(value = "execution(void net.zoungrana.agent.Agent.ajouterTransaction(net.zoungrana.transaction.Transaction)) && this(agent)",
            argNames = "appel,agent")
    public Object invaliderMaximum(ProceedingJoinPoint appel, Agent agent) throws Throwable {
        maximums.remove(agent);
        return appel.proceed();
    }
}
