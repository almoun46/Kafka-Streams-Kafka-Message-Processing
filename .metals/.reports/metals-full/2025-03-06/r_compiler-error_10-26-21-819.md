file://<WORKSPACE>/src/main/java/org/exemple/demo/App.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 1047
uri: file://<WORKSPACE>/src/main/java/org/exemple/demo/App.java
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

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;

/**
 * Hello world!
 *
 */
public class App 
{
    public App(){

    }

    public void create_consumer(){
        Properties props = new Properties () ;
        props.setProperty ( ConsumerConfig . BOOTSTRAP_SERVERS_CONFIG , "localhost:9092" ) ;
        props.setProperty ( ConsumerConfig . GROUP_ID_CONFIG , "test" ) ;
        props.setProperty ( ConsumerConfig . ENABLE_AUTO_COMMIT_CONFIG , "false" ) ;
        props.setProperty ( @@ConsumerConfig . KEY_DESERIALIZER_CLASS_CONFIG ,
        "org.apache.kafka.common.serialization.StringDeserializer" ) ;
        props.setProperty ( ConsumerConfig . VALUE_DESERIALIZER_CLASS_CONFIG ,
        "org.apache.kafka.common.serialization.StringDeserializer" ) ;
        KafkaConsumer < String , String > consumer = new KafkaConsumer < >( props ) ;
        consumer.subscribe ( Arrays.asList ( "topic1" , "topic2" ) ) ;
        List < ConsumerRecord < String , String > > buffer = new ArrayList < >() ;
        while ( true ) {
        ConsumerRecords < String , String > records =
        consumer.poll ( Duration.ofMillis (100) ) ;
        for ( ConsumerRecord < String , String > record : records ) {
        buffer.add ( record ) ;
        // Afficher l'offset, la clé et la valeur du message
        System.out.println("Offset = %d, key = %s, value = %s\n" + record.offset() + record.key() + record.value());
        }
        //consumer.close() ;
        }
    }

    public void create_producer ( ){
        Properties props = new Properties () ;
        props.put ( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , "localhost:9092" ) ;
        props.put ( ProducerConfig.ACKS_CONFIG , "all" ) ;
        props.put ( ProducerConfig. KEY_SERIALIZER_CLASS_CONFIG ,
        "org.apache.kafka.common.serialization.StringSerializer" ) ;
        props.put ( ProducerConfig. VALUE_SERIALIZER_CLASS_CONFIG ,
        "org.apache.kafka.common.serialization.StringSerializer" ) ;

        Producer < String , String > producer = new KafkaProducer < >( props ) ;
        for ( int i = 0; i < 100; i ++)
        producer.send ( new ProducerRecord < String , String >( "topic1" ,
        Integer.toString ( i ) , Integer.toString ( i ) ) ) ;

        producer.close () ;
    }
    
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App mon_app = new App();
        //mon_app.create_producer();

        // Ajouter un sleep pour laisser le temps au producer de finir et au consumer de consommer les messages.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mon_app.create_consumer();
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
	dotty.tools.pc.HoverProvider$.hover(HoverProvider.scala:40)
	dotty.tools.pc.ScalaPresentationCompiler.hover$$anonfun$1(ScalaPresentationCompiler.scala:376)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator