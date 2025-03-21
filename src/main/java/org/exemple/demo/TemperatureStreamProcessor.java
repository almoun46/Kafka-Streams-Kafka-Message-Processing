package org.exemple.demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.json.JSONArray;
import org.json.JSONObject;

public class TemperatureStreamProcessor implements Runnable {

    private static final String INPUT_TOPIC = "topic1"; // Topic source (messages des bâtiments)
    private static final String ALERT_TOPIC = "topic_alerts"; // Topic des alertes
    private static final double TEMP_MIN = 5.0; // Seuil minimum
    private static final double TEMP_MAX = 10.0; // Seuil maximum

    @Override
    public void run() {
        // 1️⃣ Configuration de Kafka Streams
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "temperature-stream-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        // 2️⃣ Construire le Kafka Streams Topology
        StreamsBuilder builder = new StreamsBuilder();

        // Lire les messages du topic
        KStream<String, String> stream = builder.stream(INPUT_TOPIC);

        stream.peek((key, value) -> System.out.println("📥 Message reçu dans Kafka Streams: " + key + " -> " + value));

        // 3️⃣ Transformer les messages JSON pour extraire les salles et températures
        KStream<String, Double> temperatureStream = stream.flatMap((building, jsonValue) -> {
            List<KeyValue<String, Double>> keyValueList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(jsonValue); // ✅ Utilisation de org.json

                // Parcourir chaque bâtiment dans l'objet JSON
                for (String buildingKey : jsonObject.keySet()) {
                    JSONArray roomsArray = jsonObject.getJSONArray(buildingKey);

                    for (int i = 0; i < roomsArray.length(); i++) {
                        JSONObject room = roomsArray.getJSONObject(i);
                        String salle = room.getString("salle");
                        double temperature = room.getDouble("temperature");

                        //keyValueList.add(new KeyValue<>(salle, temperature));
                        keyValueList.add(new KeyValue<>(buildingKey + "-" + salle, temperature));

                        System.out.println("🔍 Salle: " + salle + " | Température: " + temperature);
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du parsing JSON: " + e.getMessage());
            }
            return keyValueList;
        });

        // Sélectionner la salle comme clé
        // temperatureStream = temperatureStream.selectKey((salle, temp) -> salle);

        // temperatureStream.peek((salle, temp) -> System.out
        //         .println("🔥 Donnée reçue avant fenêtrage - Salle: " + salle + " | Température: " + temp));

        // 4️⃣ Fenêtrage de 1 minute et calcul de la moyenne des températures
        TimeWindows timeWindowing = TimeWindows.of(Duration.ofMinutes(1)).advanceBy(Duration.ofSeconds(30));

        KTable<Windowed<String>, Double> avgTemperatureByRoom = temperatureStream
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double())) // Groupement par bâtiment-salle
                .windowedBy(timeWindowing)
                .aggregate(
                        () -> 0.0, // Valeur initiale
                        (salle, temperature, avg) -> (avg + temperature) / 2, // Moyenne
                        Materialized.with(Serdes.String(), Serdes.Double()));

        avgTemperatureByRoom.toStream().peek((windowedKey, avgTemp) -> System.out
                .println("📊 Fenêtrage actif - Salle: " + windowedKey.key() + " | Temp Moyenne: " + avgTemp));

        // 5️⃣ Détection des alertes température
        KStream<String, String> alerts = avgTemperatureByRoom
                .toStream()
                .filter((windowedKey, avgTemp) -> avgTemp < TEMP_MIN || avgTemp > TEMP_MAX)
                .map((windowedKey, avgTemp) -> {
                    String salle = windowedKey.key();
                    String alertMessage = "🚨 Alerte Température : " + salle + " | Temp Moyenne: " + avgTemp;
                    System.out.println(alertMessage);
                    return new KeyValue<>(salle, alertMessage);
                });

        // 6️⃣ Écriture des alertes dans `topic_alerts`
        alerts.to(ALERT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        // 7️⃣ Lancer Kafka Streams 
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // 8️⃣ Maintenir le Thread en vie
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        try {
            while (true) {
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            streams.close();
        }
    }

    public static void main(String[] args) {
        TemperatureStreamProcessor processor = new TemperatureStreamProcessor();
        Thread thread = new Thread(processor);
        thread.start();
    }
}
