# Examen blanc — Design patterns et programmation orientée aspect

Application Java 21 de gestion d’agents et de leurs transactions. Les questions suivent l’ordre de l’énoncé.

## 1. Diagramme de classes

![Diagramme manuscrit du modèle](diagrammes-captures/diagramme-classe.png)

- **Builder**. `TransactionBuilder` rassemble l’identifiant, la date, le montant et le type, puis crée une `Transaction`.
- **Observer**. `Agent` joue le rôle de sujet et implémente `Observateur`. Un agent a zéro à plusieurs abonnés. Chaque ajout transmet un `EvenementTransaction` qui référence la transaction et contient le nom de l’émetteur.
- **Strategy**. Chaque agent possède une stratégie active. Les trois réalisations traitent la notification par affichage, calcul de solde ou conservation de l’historique.
- **Singleton**. `Container.getInstance()` donne accès à l’instance unique. Sur le diagramme UML, le soulignement indique les membres statiques.
- **Adapter**. `Container` utilise le contrat `HDMI`. `AdaptateurVGA` réalise ce contrat et redirige l’appel vers l’afficheur VGA.

## 2. Transaction et Builder

La transaction est immuable. Les quatre champs doivent être renseignés. Le montant doit être fini et positif ou nul. Un montant nul doit être fourni explicitement. Le type est limité à `ACHAT` ou `VENTE`.

`TransactionTest` vérifie les valeurs construites, le champ montant obligatoire et le rejet des montants invalides. [Capture des résultats](diagrammes-captures/tests-transaction.jpg)

## 3. Agent, notifications et stratégies

`A.souscrire(B)` inscrit B comme observateur de A. Chaque observateur reçoit une notification par transaction ajoutée. Le changement de stratégie prend effet à la prochaine notification. La stratégie par défaut affiche l’événement, `ScoringStrategy` ajoute les ventes et soustrait les achats, et `HistoryStrategy` conserve les transactions des événements reçus. Le solde est calculé du point de vue de l’émetteur.

`afficher()` imprime le nom de l’agent et ses transactions. `transactionMax()` compare les montants et renvoie `null` si la collection est vide.

`AgentTest` vérifie l’événement transmis, l’absence de notifications en double, le changement de stratégie, le score, l’historique et le maximum. [Capture des résultats](diagrammes-captures/tests-agent.jpg)

## 4. Conteneur et afficheurs

Le conteneur indexe ses agents par nom dans une `HashMap<String, Agent>`. Un ajout avec un nom déjà présent remplace l’entrée. Une recherche absente renvoie `null`. L’état envoyé au port HDMI contient les noms triés des agents. L’adaptateur convertit l’appel `HDMI.afficher` en `AfficheurVGA.afficherVGA`.

`ContainerTest` vérifie l’identité du singleton, l’ajout, la recherche, la suppression et les deux branchements. [Capture des résultats](diagrammes-captures/tests-container.jpg)

## 5. Patterns supplémentaires proposés

| Pattern | Application possible |
| --- | --- |
| Command | Représenter un ajout ou une suppression comme une commande pour enregistrer les opérations et permettre l’annulation. |
| Repository | Placer le stockage derrière une interface pour utiliser une base de données tout en conservant les opérations métier du conteneur. |
| Factory Method | Créer des afficheurs en fonction de la configuration et centraliser la sélection de l’adaptateur VGA. |

## 6. Aspects techniques

AspectJ tisse les aspects au chargement des classes. Le fichier `src/main/resources/META-INF/aop.xml` enregistre les trois aspects et Maven Surefire active l’agent Java pendant les tests.

| Annotation | Comportement vérifié |
| --- | --- |
| `@Log` | Mesure la durée d’exécution avec `System.nanoTime()` et journalise même si la méthode lève une exception. |
| `@Cachable` | Mémorise le maximum par agent, y compris le résultat vide. L’ajout invalide le cache avant la notification pour qu’un observateur qui consulte le maximum voie la nouvelle transaction. Un résultat servi du cache évite l’exécution du calcul et sa journalisation. |
| `@SecuredBy(roles = {...})` | Vérifie la présence d’au moins un rôle requis avant l’appel. `ADMIN` peut gérer les agents et le branchement. `LECTEUR` peut rechercher et afficher. L’authentification vérifie le mot de passe avec PBKDF2 et un sel aléatoire. Le contexte d’authentification est restauré après l’opération. |

Les trois aspects disposent de tests JUnit. [Capture de la suite complète](diagrammes-captures/tests-suite.jpg)

## Exécuter sur Windows

Depuis PowerShell, avec Java 21 et Maven :

```powershell
mvn clean test
```

Pour lancer la démonstration interactive, renseigner `EXAMEN_USERNAME` et `EXAMEN_PASSWORD` dans l’environnement du terminal, puis utiliser les mêmes identifiants à l’invite. Le compte de démonstration reçoit le rôle `ADMIN`.

```powershell
mvn package
$m2 = (mvn -q help:evaluate '-Dexpression=settings.localRepository' '-DforceStdout' | Out-String).Trim()
$aspectj = Join-Path $m2 'org/aspectj/aspectjweaver/1.9.22.1/aspectjweaver-1.9.22.1.jar'
java "-javaagent:$aspectj" -cp "target/classes;$aspectj" net.zoungrana.Main
```

Les quatre captures ont été prises dans la fenêtre Run d’IntelliJ IDEA sur Windows après avoir lancé Maven → Lifecycle → test. Les captures par classe montrent différents passages de cette exécution. IntelliJ a affiché 11 tests réussis, BUILD SUCCESS et un code de sortie 0. Les rapports détaillés sont générés dans `target/surefire-reports`.
