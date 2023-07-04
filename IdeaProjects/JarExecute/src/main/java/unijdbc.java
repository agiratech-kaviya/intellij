import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class unijdbc {
    public static void main(String args[]) {
        String url = "jdbc:ibm-u2://20.96.128.216:31438/Eclipse/?dbmstype=UNIVERSE";
        String user = "QUALZEAL";
        String password = "1z5aLhf%";
        try {
            Class.forName("com.ibm.u2.jdbc.UniJDBCDriver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection is successful to the database: " + url);

            String query = "SELECT * FROM sample";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
