file://<WORKSPACE>/src/main/java/org/exemple/demo/SimpleConsumer.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file://<WORKSPACE>/src/main/java/org/exemple/demo/SimpleConsumer.java
text:
```scala
package org.exemple.demo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;


public class SimpleConsumer implements Runnable {
    private List<String> topicNames;
    private int port;
    private String groupId;
    private int totalMessage ;

    public SimpleConsumer(List<String> topicNames, String groupId, int port) {
        this.topicNames = topicNames;
        this.groupId = groupId;
        this.port = port;
        this.totalMessage = 0;
    }
    public SimpleConsumer() {
        this.topicNames = new ArrayList<String>(Arrays.asList("topic1", "topic2"));
        this.groupId = "my_group";
        this.port = 9092;
        this.totalMessage = 0;
    }
    @Override
    public void run() {
        try {
        createConsumer();
        } catch (Exception e) {
           System.out.println(" Consumer : " + Thread.currentThread().getName() + " Error : " + e.getMessage());
        }
    }


    public void createConsumer() {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:"+this.port);
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(this.topicNames);
        int messageCount = 0;
        long startTime = System.currentTimeMillis();
        while (messageCount < 1000000) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
             
                // Afficher l'offset, la clé et la valeur du message
                //System.out.printf("Consumer Name = %s, Offset = %d, key = %s, value = %s\n", Thread.currentThread().getName() , record.offset() , record.key() , record.value());
                messageCount++;
            }
        
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Temps d'exécution : " + (endTime - startTime) + " ms pour le consommateur " + Thread.currentThread().getName());
        consumer.close();
    }
    public void createConsumersWithDifferentGroupsAndTopics(List<String> groups, List<String> topics, int port, int numConsumers){
        
        List<Thread> threads = new ArrayList<Thread>();
        // Selon nombre de consommateurs, créer des consommateurs avec des groupes et des topics différents
        int remainingConsumers = numConsumers % groups.size();
        int consumerPergroups = numConsumers - remainingConsumers;
        


        for (String group : groups){
                for (int i = 0; i < consumerPergroups; i++){
                    SimpleConsumer consumer = new SimpleConsumer(topics, group, port);
                    Thread thread = new Thread(consumer);
                    threads.add(thread);
                    thread.start();
                    System.out.println("Consumer " + thread.getName() + " started");
                }
        }
        // Ajouter les consommateurs restants jusqu'à la limite du nombre de groupes
        for (int i = 0; i < remainingConsumers; i++){
            SimpleConsumer consumer = new SimpleConsumer(topics, groups.get(i), port);
            Thread thread = new Thread(consumer);
            threads.add(thread);
            thread.start();
        }
        //Attendre que tous les consommateurs se terminent
        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Erreur lors de l'attente des consommateurs : " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        
    }
    public static void main(String[] args) {
        List<String> topics = new ArrayList<String>(Arrays.asList("topic1", "topic2"));
        int port = 9092;
        //List<String> groupsId = new ArrayList<>(Arrays.asList( "my_group01"));
        List<String> groupsId = new ArrayList<>(Arrays.asList( "my_group01", "my_group02", "my_group03", "my_group04", "my_group05"));
        int numConsumers = 3;
        // 
        long startTime = System.currentTimeMillis();

        // Créer des consommateurs avec des groupes et des topics différents
        SimpleConsumer consumer = new SimpleConsumer();
        consumer.createConsumersWithDifferentGroupsAndTopics(groupsId, topics, port, numConsumers);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Temps d'exécution Total : " + (endTime - startTime) + " ms");
        
    }
    
}

```



#### Error stacktrace:

```
scala.collection.Iterator$$anon$19.next(Iterator.scala:973)
	scala.collection.Iterator$$anon$19.next(Iterator.scala:971)
	scala.collection.mutable.MutationTracker$CheckedIterator.next(MutationTracker.scala:76)
	scala.collection.IterableOps.head(Iterable.scala:222)
	scala.collection.IterableOps.head$(Iterable.scala:222)
	scala.collection.AbstractIterable.head(Iterable.scala:935)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:164)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:45)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:345)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:109)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator