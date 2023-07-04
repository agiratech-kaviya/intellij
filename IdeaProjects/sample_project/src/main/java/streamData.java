import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class streamData {
    public static void main(String[] args) {
        // Connect to MySQL
        String url = "jdbc:mysql://35.238.73.126/classicmodels";
        String username = "root";
        String password = "e|$fPl1o&{az5;9$@data@engineering@poc";
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", StringSerializer.class.getName());

            Producer<String, String> producer = new KafkaProducer<>(props);

            // Fetch column names from MySQL table
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='classicmodels' AND TABLE_NAME='employees'");

            while (true) {
                resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='classicmodels' AND TABLE_NAME='employees'");

                while (resultSet.next()) {
                    // Retrieve column name
                    String columnName = resultSet.getString("COLUMN_NAME");

                    // Fetch new data for the column
                    Statement dataStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet dataResultSet = dataStatement.executeQuery("SELECT " + columnName + " FROM employees");

                    // Move to the last row
                    dataResultSet.last();
                    int rowCount = dataResultSet.getRow();

                    // Check if new rows were added
                    if (rowCount > 0) {
                        // Move to the first new row
                        dataResultSet.beforeFirst();

                        // Process new rows
                        while (dataResultSet.next()) {
                            // Retrieve data for the column
                            String columnValue = dataResultSet.getString(columnName);

                            // Print data to the terminal
                            System.out.println("Column: " + columnName + ", Value: " + columnValue);

                            // Publish data to Kafka topic
                            ProducerRecord<String, String> record = new ProducerRecord<>(columnName, columnValue);
                            producer.send(record);
                        }
                    }

                    dataResultSet.close();
                    dataStatement.close();
                }

                // Sleep for a specified interval before checking for new changes
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
