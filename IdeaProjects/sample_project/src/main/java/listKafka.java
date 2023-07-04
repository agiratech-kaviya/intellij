import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class listKafka {
    public static void main(String[] args) {
        // Set Kafka bootstrap server address and port
        String bootstrapServers = "localhost:9092";

        // Set properties for AdminClient
        Properties props = new Properties();
        props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Create AdminClient
        try (AdminClient adminClient = AdminClient.create(props)) {
            // Set options for listing topics
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true); // Include internal topics

            // List topics
            ListTopicsResult topicsResult = adminClient.listTopics(options);
            KafkaFuture<Set<String>> topicsFuture = topicsResult.names();

            // Get the list of topics
            Set<String> topics = topicsFuture.get();

            // Print the list of topics
            System.out.println("Kafka topics:");
            for (String topic : topics) {
                System.out.println(topic);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

