
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class MysqlConnectToKafka {
    public static void main(String[] args) {
        // Connect to MySQL
        String url = "jdbc:mysql://35.238.73.126:3306/classicmodels";
        String username = "root";
        String password = "e|$fPl1o&{az5;9$@data@engineering@poc";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            // Fetch column names from MySQL table
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='classicmodels' AND TABLE_NAME='employees'");

            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", StringSerializer.class.getName());

            Producer<String, String> producer = new KafkaProducer<>(props);

            while (resultSet.next()) {
                // Retrieve column name
                String columnName = resultSet.getString("COLUMN_NAME");

                // Publish data to Kafka topic
                ProducerRecord<String, String> record = new ProducerRecord<>(columnName, columnName);
                producer.send(record);
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
