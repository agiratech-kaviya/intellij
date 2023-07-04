import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;

public class kafkaProducerClass {
    public static void main(String[] args){
        // Create the topic
        createTopic();
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        Producer<String, String> producer = new KafkaProducer<>(props);

        for (int i = 1; i <= 10; i++) {
            String key = Integer.toString(i);
            String val = "Message: " + Integer.toString(i);
            producer.send(new ProducerRecord<>("TutorialTopic07", key, val));
        }
        producer.close();
    }

    public static void createTopic() {
        // Set the properties for AdminClient
        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", "localhost:9092");

        try (AdminClient adminClient = AdminClient.create(adminProps)) {
            // Create the NewTopic object with the desired topic name and configuration
            NewTopic newTopic = new NewTopic("TutorialTopic07", 1, (short) 1);

            // Create the topic using the AdminClient
            adminClient.createTopics(Collections.singletonList(newTopic));
            System.out.println("Topic created successfully.");
        } catch (Exception e) {
            System.out.println("An error occurred while creating the topic: " + e.getMessage());
        }
    }
}

