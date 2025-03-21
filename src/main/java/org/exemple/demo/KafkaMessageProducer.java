package org.exemple.demo;

import java.util.Properties;
import java.util.ArrayList;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

public class KafkaMessageProducer {
    private int numProducers;
    private int numMessages;
    private String topicName;
    private int port;

    public KafkaMessageProducer(int numProducers, int numMessages, String topic, int port) {
        this.numProducers = numProducers;
        this.numMessages = numMessages;
        this.topicName = topic;
        this.port = port;
    }

    public ArrayList<Producer<String, String>> createProducers() {
        ArrayList<Producer<String, String>> producers = new ArrayList<>();

        for (int i = 0; i < numProducers; i++) {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + this.port);
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");

            Producer<String, String> producer = new KafkaProducer<>(props);
            producers.add(producer);
        }
        return producers;
    }

    public void produceMessages(Producer<String, String> producer, int numMessages, int startOffset, int producerId) {
        for (int i = 0; i < numMessages; i++) {
            int offset = startOffset + i;
            producer.send(new ProducerRecord<>(this.topicName, Integer.toString(offset), Integer.toString(offset)));
            System.out.println("Producteur " + producerId + " a produit le message: " + offset);
        }
        producer.close();
    }

    public void launchProducers(ArrayList<Producer<String, String>> producers) {
        int messagesPerProducer = this.numMessages / producers.size();
        int remainingMessages = this.numMessages % producers.size();

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < producers.size(); i++) {
            int startOffset = i * messagesPerProducer;
            int messagesToSend = messagesPerProducer;

            // Le premier producteur prend les messages restants
            if (i == 0) {
                messagesToSend += remainingMessages;
            }

            final int producerId = i;
            final int finalMessagesToSend = messagesToSend;
            final int finalStartOffset = startOffset;
            final Producer<String, String> producer = producers.get(i);

            Thread t = new Thread(() -> {
                produceMessages(producer, finalMessagesToSend, finalStartOffset, producerId);
            });

            threads.add(t);
            t.start();
        }

        // Attente de la fin de tous les threads
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        KafkaMessageProducer simpleProducer = new KafkaMessageProducer(2, 5, "topic1", 9092);
        ArrayList<Producer<String, String>> producers = simpleProducer.createProducers();
        simpleProducer.launchProducers(producers);
    }
}
