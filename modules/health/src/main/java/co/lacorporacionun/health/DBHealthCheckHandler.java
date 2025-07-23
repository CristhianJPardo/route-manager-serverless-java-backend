package co.lacorporacionun.health;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBHealthCheckHandler
    implements RequestHandler<Object, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Object input, Context ctx) {
        try (Connection conn = DriverManager.getConnection(
                 System.getenv("DB_HOST"),
                 System.getenv("DB_USER"),
                 System.getenv("DB_PASSWORD"));
             Statement stmt = conn.createStatement()) {

            stmt.executeQuery("SELECT 1");
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("DB OK");
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withBody("DB ERROR: " + e.getMessage());
        }
    }
}
