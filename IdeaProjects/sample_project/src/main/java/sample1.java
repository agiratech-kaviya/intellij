import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class sample1 {
    public static void main(String args[]) {
        String url = "jdbc:mysql://35.238.73.126:3306/classicmodels";
        String user = "root";
        String password = "e|$fPl1o&{az5;9$@data@engineering@poc";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection is successful to the database: " + url);

            String query = "INSERT INTO sample (id, name) VALUES (104, 'ks')";
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            System.out.println(rowsAffected + " row(s) inserted.");

            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}