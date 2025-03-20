package org.exemple.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;
import org.apache.kafka.common.TopicPartition;

public class KafkaMessageConsumer implements Runnable {
    private List<String> topicNames;
    private int port;
    private String groupId;
    private int totalMessage;

    public KafkaMessageConsumer(List<String> topicNames, String groupId, int port) {
        this.topicNames = topicNames;
        this.groupId = groupId;
        this.port = port;
        this.totalMessage = 0;
    }

    public KafkaMessageConsumer() {
        this.topicNames = new ArrayList<String>(Arrays.asList("topic1", "topic2"));
        this.groupId = "my_group";
        this.port = 9092;
        this.totalMessage = 0;
    }

    @Override
    public void run() {
        try {
            consumeMessages();
        } catch (Exception e) {
            System.out.println(" Consumer : " + Thread.currentThread().getName() + " Error : " + e.getMessage());
        }
    }

    // public void createConsumer() {
    // Properties props = new Properties();
    // props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
    // "localhost:"+this.port);
    // props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
    // props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    // props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    // "org.apache.kafka.common.serialization.StringDeserializer");
    // props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    // "org.apache.kafka.common.serialization.StringDeserializer");
    // KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    // consumer.subscribe(this.topicNames);
    // int messageCount = 0;
    // long startTime = System.currentTimeMillis();
    // while (messageCount < 10) {
    // ConsumerRecords<String, String> records =
    // consumer.poll(Duration.ofMillis(100));
    // for (ConsumerRecord<String, String> record : records) {

    // // Afficher l'offset, la clé et la valeur du message
    // System.out.printf("Consumer Name = %s, Offset = %d, key = %s, value = %s,
    // group=%s\n", Thread.currentThread().getName() , record.offset() ,
    // record.key() , record.value(), this.groupId); // public void createConsumersWithDifferentGroupsAndTopics(List<String> groups,
    // List<String> topics, int port, int numConsumers){

    // List<Thread> threads = new ArrayList<Thread>();
    // // Selon nombre de consommateurs, créer des consommateurs avec des groupes et
    // des topics différents
    // int remainingConsumers = numConsumers % groups.size();
    // int consumerPergroups = numConsumers - remainingConsumers;

    // for (String group : groups){
    // for (int i = 0; i < consumerPergroups; i++){
    // KafkaMessageConsumer consumer = new KafkaMessageConsumer(topics, group,
    // port);
    // Thread thread = new Thread(consumer);
    // threads.add(thread);
    // thread.start();
    // System.out.println("Consumer " + thread.getName() + " started");
    // }
    // }
    // // Ajouter les consommateurs restants jusqu'à la limite du nombre de groupes
    // for (int i = 0; i < remainingConsumers; i++){
    // KafkaMessageConsumer consumer = new KafkaMessageConsumer(topics,
    // groups.get(i), port);
    // Thread thread = new Thread(consumer);
    // threads.add(thread);
    // thread.start();
    // }
    // //Attendre que tous les consommateurs se terminent
    // for(Thread thread : threads) {
    // try {
    // thread.join();
    // } catch (InterruptedException e) {
    // System.out.println("Erreur lors de l'attente des consommateurs : " +
    // e.getMessage());
    // Thread.currentThread().interrupt();
    // }
    // }

    // }
    // le consommateur " + Thread.currentThread().getName());
    // consumer.close();
    // }

    private void consumeMessages() {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + this.port);
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(this.topicNames);

        

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumer %s, Group: %s, Offset: %d, Key: %s, Value: %s%n",
                            Thread.currentThread().getName(), this.groupId, record.offset(), record.key(),
                            record.value());
                }
                consumer.commitSync();
            }
        } finally {
            consumer.close();
        }
    }

   
    public void createConsumersWithDifferentGroupsAndTopics(List<String> groups, List<String> topics, int port,
            int numConsumers) {
        List<Thread> threads = new ArrayList<>();
        
        for (String group: groups ){ 
            for (int i = 0; i < numConsumers; i++) {
                KafkaMessageConsumer consumer = new KafkaMessageConsumer(topics, group, port);
                Thread thread = new Thread(consumer);
                threads.add(thread);
                thread.start();
                System.out.println("Consumer " + thread.getName() + " started in group " + group);
            }
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Error waiting for consumer threads: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        List<String> topics = new ArrayList<String>(Arrays.asList("topic1", "topic2"));
        int port = 9092;
        //List<String> groupsId = new ArrayList<>(Arrays.asList( "my_group"));
        List<String> groupsId = new ArrayList<>(Arrays.asList("my_group01", "my_group02"));
        int numConsumers = 3;
        //
        long startTime = System.currentTimeMillis();

        // Créer des consommateurs avec des groupes et des topics différents
        KafkaMessageConsumer consumer = new KafkaMessageConsumer();
        consumer.createConsumersWithDifferentGroupsAndTopics(groupsId, topics, port, numConsumers);

        long endTime = System.currentTimeMillis();
        System.out.println("Temps d'exécution Total : " + (endTime - startTime) + " ms");

    }

}
