# **📌 Kafka Streams & Kafka Message Processing**

## **📖 Description du Projet**
Ce projet implémente deux cas d'utilisation de **Apache Kafka** :
1. **Exercice 2 : Kafka Message Processing** - Comparaison des fonctionnalités de Kafka avec JMS.
2. **Exercice 3 : Kafka Streams - Monitoring des Températures** - Traitement de flux de données en temps réel.

---

# **📝 Exercice 2 : Kafka Message Processing & Comparaison avec JMS**

## **📌 Objectif**
L'objectif est d'**utiliser Kafka pour produire et consommer des messages**, et de **comparer ses fonctionnalités avec JMS**. 

### **🛠️ Fonctionnalités implémentées :**
1. **Kafka Producer** - Produit des messages vers un topic Kafka.
2. **Kafka Consumer** - Lit les messages de Kafka.
3. **Scalabilité des consommateurs**
   - **1er test** : Plusieurs consommateurs dans **un même groupe**.
   - **2ème test** : Plusieurs consommateurs dans **des groupes différents**.
4. **Analyse des performances** - Mesure du **temps total de consommation** pour chaque stratégie.
5. **Comparaison avec JMS** - Différences entre Kafka et JMS.

---

## **📌 1. Architecture du Système**
### **📜 Composants**
- **KafkaMessageProducer.java** : Produit un grand volume de messages (1 million) dans Kafka.
- **KafkaMessageConsumer.java** : Consomme ces messages et mesure le temps de traitement.
- **Kafka Topic** avec **2 partitions** pour équilibrer la charge.
- **Expérimentation avec différents groupes de consommateurs**.


---

### **📌 Installation et Exécution**
#### **Démarrer Kafka**
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/broker1.properties
bin/kafka-server-start.sh config/broker2.properties
```

#### **Créer le/les topics Kafka**
```bash
bin/kafka-topics.sh --create --topic topic1 --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1
```

#### **Lancer les Producteurs Kafka**
```bash
mvn exec:java -Dexec.mainClass="org.exemple.demo.KafkaMessageProducer"
```

#### **Lancer les Consumers**
```bash
mvn exec:java -Dexec.mainClass="org.exemple.demo.KafkaMessageConsumer"
```

---

## **📌 2. Résultats des tests**
| Stratégie | Nombre de consommateurs | Temps total (ms) |
|-----------|------------------------|-----------------|
| Même groupe | 2 consommateurs | 941, 991 |
| Même groupe | 3 consommateurs | 3974, 4042 |
| Groupes différents | 2 consommateurs | 1122, 1117 |
| Groupes différents | 3 consommateurs | 1117, 1200 |

📌 **Observation** :
- Kafka **répartit dynamiquement** les messages entre les consommateurs d’un **même groupe**.
- **Avec 3 consommateurs dans le même groupe, le temps total est plus élevé** car Kafka attend la fin du traitement de chaque partition.
- Avec **des groupes différents, chaque consommateur reçoit tous les messages**, donc le temps est plus stable.
- En **conditions réelles**, la comparaison avec JMS nécessite un cluster Kafka.

---

## **📌 3. Comparaison entre Kafka et JMS**
| Fonctionnalité | Kafka | JMS |
|--------------|------|----|
| **Mode de communication** | Publish/Subscribe et Queue | Publish/Subscribe et Queue |
| **Scalabilité** | Très scalable (ajout dynamique de consommateurs) | Moins scalable, besoin de brokers dédiés |
| **Rétention des messages** | Stocke les messages **pendant une durée configurable** | Ne stocke pas les messages après consommation |
| **Performance** | Très rapide, conçu pour le big data | Moins performant pour des volumes massifs |
| **Garantie d’ordre** | Assurée par **les partitions Kafka** | Assurée par **les files d’attente JMS** |
| **Persistant/Durable** | Stockage persistant possible | Stocke uniquement si configuré avec une base de données |
| **Cas d’usage** | Big Data, Streaming, Microservices | Messagerie classique, transactions bancaires |

📌 **Conclusion :**
- **Kafka est plus performant et scalable** que JMS pour la gestion de gros volumes de données.
- **JMS est plus adapté aux architectures transactionnelles** nécessitant une garantie forte de livraison.
- Kafka est **orienté Event Streaming**, alors que JMS est plus **orienté Enterprise Messaging**.

---

# **📝 Exercice 3 : Kafka Streams - Monitoring des Températures**

## **📖 Objectif**
L'objectif est de **traiter en temps réel les températures envoyées par des capteurs** et de générer des **alertes** si elles dépassent certains seuils.

### **🛠️ Fonctionnalités implémentées :**
1. **Kafka Multi-Producteurs** - Génère des températures toutes les 10 secondes.
2. **Kafka Streams Processor** - Calcule **la moyenne des températures** sur une fenêtre de 5 minutes.
3. **Détection d’alerte** - Alerte si température < 5°C ou > 30°C.
4. **Kafka Consumer pour Alertes** - Affiche les alertes en temps réel.

---

## **📌 1. Architecture du Système**
- **Multi-Producteurs Kafka** envoient les températures.
- **Kafka Streams** consomme ces messages et applique un **fenêtrage de 5 minutes**.
- **Topic d'alertes `topic_alerts`** pour signaler les températures critiques.
- **Consommateur d’alerte** qui affiche les messages.

---

## **📌 2. Installation et Exécution**
### **Démarrer Kafka**
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/broker1.properties
bin/kafka-server-start.sh config/broker2.properties
```

### **Créer les topics Kafka**
```bash
bin/kafka-topics.sh --create --topic topic1 --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1
bin/kafka-topics.sh --create --topic topic_alerts --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1
```

### **Lancer les Producteurs Kafka**
```bash
mvn exec:java -Dexec.mainClass="org.exemple.demo.MultiProducer"
```

### **Lancer Kafka Streams Consumer**
```bash
mvn exec:java -Dexec.mainClass="org.exemple.demo.TemperatureStreamProcessor"
```

### **Lire les Alertes de Température**
```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic_alerts --from-beginning
```

---

## **📌 3. Références**
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [JSON](https://www.baeldung.com/java-org-json)
- [Kafka Streams : Windowing Concepts](https://senthilnayagan.medium.com/windowing-in-kafka-streams-513fc0b410c9)

---

## **📌 4. Auteurs**
- **ALMOUNTASSIR ABDEL-AZIZ**
- **GACKOU MAMADOU**

---

🚀 **Ce projet permet d’expérimenter Kafka pour le message processing et le traitement en temps réel avec Kafka Streams !** 🎯🔥

