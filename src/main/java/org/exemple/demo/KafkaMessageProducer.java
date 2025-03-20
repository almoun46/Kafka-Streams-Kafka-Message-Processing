package org.exemple.demo;


import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Scanner;
import java.util.ArrayList;

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

    public ArrayList<Producer< String , String >>  createProducers() {
        ArrayList<Producer< String , String >> producers = new ArrayList<Producer< String , String >>();

        for( int i = 0; i < numProducers; i++) {

            Properties props = new Properties () ;
            props.put ( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , "localhost:"+this.port ) ;
            props.put ( ProducerConfig.ACKS_CONFIG , "all" ) ;
            props.put ( ProducerConfig. KEY_SERIALIZER_CLASS_CONFIG ,
            "org.apache.kafka.common.serialization.StringSerializer" ) ;
            props.put ( ProducerConfig. VALUE_SERIALIZER_CLASS_CONFIG ,
            "org.apache.kafka.common.serialization.StringSerializer" ) ;

            Producer< String , String > producer = new KafkaProducer < > ( props ) ;
            producers.add(producer);
        }
        return producers;
    }

    public void produceMessages(Producer< String , String > producer, int numMessages, int startOffset) {
        for ( int i = 0; i < numMessages; i ++){
            int offset = startOffset + i;
            producer.send ( new ProducerRecord < String , String >( this.topicName ,
            Integer.toString ( offset ) , Integer.toString ( offset ) ) ) ;
        }
        producer.close () ;
        System.out.println("Messages produitent avec succès");
    }
    public void launchProducers(ArrayList<Producer< String , String >> producers) {
        // Dispatcher le nombre de messages à produire entre les différents producteurs
        // Calculer le nombre de messages à produire par producteur
        int remainingMessages = this.numMessages % producers.size();
        int messagesPerProducer = producers.size() > 0 ? this.numMessages - remainingMessages : 0;
        
        for(int i = 0; i < producers.size(); i++) {
            int startOffset = i * messagesPerProducer;
            produceMessages(producers.get(i), messagesPerProducer,startOffset);
        }
        // S'ils restent de messages à produire, on les envoie au premier producteur
        if(remainingMessages > 0) {
            produceMessages(producers.get(0), remainingMessages, producers.size() * messagesPerProducer);
        }
    }

    public static void main(String[] args) {
        KafkaMessageProducer simpleProducer = new KafkaMessageProducer(2,10, "topic1", 9092); // 
        ArrayList<Producer< String , String >> producers = simpleProducer.createProducers();
        simpleProducer.launchProducers(producers);
    }
    
}
