import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MySQLToKafkaExample {
    public static void main(String[] args) {
        // Connect to MySQL
        try {
            String url = "jdbc:mysql://35.238.73.126/classicmodels";
            String username = "root";
            String password = "e|$fPl1o&{az5;9$@data@engineering@poc";

            Connection connection = DriverManager.getConnection(url, username, password);

            // Execute a query to fetch column names
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='classicmodels' AND TABLE_NAME='customers'");

            List<String> columnNames = new ArrayList<>();

            while (resultSet.next()) {
                // Retrieve column names
                String columnName = resultSet.getString("COLUMN_NAME");
                columnNames.add(columnName);
            }

            resultSet.close();
            statement.close();
            connection.close();

            // Create Kafka topics based on column names
            Properties adminProps = new Properties();
            adminProps.put("bootstrap.servers", "localhost:9092");

            try (AdminClient adminClient = AdminClient.create(adminProps)) {
                List<NewTopic> newTopics = new ArrayList<>();

                for (String columnName : columnNames) {
                    NewTopic newTopic = new NewTopic(columnName, 1, (short) 1);
                    newTopics.add(newTopic);
                }

                adminClient.createTopics(newTopics);
                System.out.println("Topics created successfully.");
            } catch (Exception e) {
                System.out.println("An error occurred while creating topics: " + e.getMessage());
            }

            // Produce messages to Kafka
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", StringSerializer.class.getName());

            Producer<String, String> producer = new KafkaProducer<>(props);

            // Connect to MySQL again to fetch data
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM customers");

            while (resultSet.next()) {
                for (String columnName : columnNames) {
                    String columnValue = resultSet.getString(columnName);

                    // Create a producer record and send it
                    ProducerRecord<String, String> record = new ProducerRecord<>(columnName, columnValue, columnValue);
                    producer.send(record);
                }
            }

            producer.close();
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



