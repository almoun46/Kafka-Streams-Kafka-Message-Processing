package org.exemple.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultiProducer implements Runnable {

    private String topicName;
    private int port;
    private static final int NUM_ROOMS = 5; // choix par défaut à 5 salles par bâtiment
    private static final int NUM_BUILDINGS = 5;  // choix par défaut à 5 bâtiments
    private Producer<String, String> producer;

    public MultiProducer(String topic, int port) {
        this.topicName = topic;
        this.port = port;
        this.producer = createProducer();
    }

    @Override
    public void run() {
        while (true) {
            try {
                    for(int i= 0; i < NUM_BUILDINGS; i++) { 
                        String buildingName = "Building" + i;   
                        String data = sendTemperatureData(i);
                        try {
                            new JSONObject(data); // Valide si 'data' est un JSON valide
                        } catch (Exception e) {
                            System.out.println("Erreur de validation JSON : " + e.getMessage());
                            continue; // Ignore ce message
                        }
                        System.out.print(data + "\n");
                        ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.topicName, buildingName, data);
    
                        
                        producer.send(record, (metadata, exception) -> {
                            if (exception != null) {
                                System.out.println("Erreur d'envoi Kafka: " + exception.getMessage());
                            } else {
                                System.out.println("✅ Message envoyé avec succès par " + Thread.currentThread().getName());
                                // System.out.println("🟢 Topic: " + metadata.topic() +
                                //         " | Partition: " + metadata.partition() +
                                //         " | Offset: " + metadata.offset() +
                                //         " | Timestamp: " + metadata.timestamp());
                                System.out.flush(); // Forcer l'affichage des logs
                            }
                        });
                        try {
                            new JSONObject(data); // Valide si 'data' est un JSON valide
                        } catch (Exception e) {
                            System.out.println("Erreur de validation JSON : " + e.getMessage());
                            continue; // Ignore ce message
                        }try {
    new JSONObject(data); // Valide si 'data' est un JSON valide
} catch (Exception e) {
    System.out.println("Erreur de validation JSON : " + e.getMessage());
    continue; // Ignore ce message
}
                        Thread.sleep(10000);// pause chaque 10 
                    }
            } catch (Exception e) {
                System.out.println(" Producer : " + Thread.currentThread().getName() + " Error : " + e.getMessage());
                break;
            }
        }
    }

    public Producer<String, String> createProducer() {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + this.port);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(props);

    }

    public static void createProducers(String topic, int port, int numProducers) {
        List<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < numProducers; i++) {
            MultiProducer producer = new MultiProducer(topic, port);
            Thread thread = new Thread(producer, "Producer-" + (i + 1));
            threads.add(thread);
            thread.start();
            System.out.println("Producer " + thread.getName() + " started");
        }

    }

    /**
     * Génère un objet JSON contenant les données de température pour un bâtiment spécifié.
     * 
     * Cette méthode crée une liste de salles, chacune avec une température générée 
     * aléatoirement. La température est arrondie à trois chiffres après la virgule 
     * et stockée dans la structure JSON. L'objet JSON final associe le nom du bâtiment 
     * à la liste des données des salles.
     * 
     * @param buildingId L'identifiant unique du bâtiment pour lequel les données de 
     *                   température sont générées.
     * @return Un JsonObject contenant les données de température pour le bâtiment 
     *         spécifié. La structure est la suivante :
     *         {
     *             "Building<buildingId>": [
     *                 {
     *                     "salle": "salle0",
     *                     "temperature": <roundedTemperature>
     *                 },
     *                 {
     *                     "salle": "salle1",
     *                     "temperature": <roundedTemperature>
     *                 },
     *                 ...
     *             ]
     *         }
     *
    private static JsonObject sendTemperatureData(int buildingId) {
        String buildingName = "Building" + buildingId;
        Random random = new Random();

        // Liste pour stocker les salles et températures.
        List<JsonObject> romList = new ArrayList<>();

        for (int i = 0; i < NUM_ROOMS; i++) {
            JsonObject roomData = new JsonObject();
            roomData.addProperty("salle", "salle" + i);

            // Généner des température aléatoirement et arrondir à 3 chiffres après la
            // virgule
            double genTemperature = -5 + random.nextDouble() * 40; // température entre -5 et 35
            double roundedTemprature = Math.round(genTemperature * 1000.00) / 1000.00;

            roomData.addProperty("temperature", roundedTemprature);
            romList.add(roomData);
        }

        // Création de la structure finale
        JsonObject finalJson = new JsonObject();
        finalJson.add(buildingName, new Gson().toJsonTree(romList));

        return finalJson;

    }*/


    private static String sendTemperatureData(int buildingId) {
        String buildingName = "Building" + buildingId;
        Random random = new Random();

        JSONArray roomList = new JSONArray();

        for (int i = 0; i < NUM_ROOMS; i++) {
            JSONObject roomData = new JSONObject();
            roomData.put("salle", "salle" + i);

            double genTemperature = -5 + random.nextDouble() * 40;
            double roundedTemperature = Math.round(genTemperature * 1000.0) / 1000.0;

            roomData.put("temperature", roundedTemperature);
            roomList.put(roomData);
        }

        JSONObject finalJson = new JSONObject();
        finalJson.put(buildingName, roomList);

        return finalJson.toString();
    }


    public static void main(String[] args) {
        int port = 9092;
        String topic = "topic1";
        int numProducers = 5;
        createProducers(topic, port, numProducers);

    }
}