// modules/health/src/main/java/co/lacorporacionun/health/HealthCheckHandler.java
package co.lacorporacionun.health;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class HealthCheckHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent req,
            Context ctx) {
        String path = req.getPath();
        ctx.getLogger().log("[HealthCheckHandler] Request path: " + path);
        try {
            if (path != null && path.endsWith("/health/db")) {
                return checkDatabase(ctx);
            } else {
                // Application health
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("OK");
            }
        } catch (Exception e) {
            ctx.getLogger().log("[HealthCheckHandler] Error: " + e);
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withBody("ERROR: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent checkDatabase(Context ctx) throws Exception {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String db   = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");
        String url;
        if (host.startsWith("jdbc:")) {
            // Si DB_HOST ya es una URL JDBC, le agregamos credenciales si no existen
            url = host;
            if (!host.contains("user=") && !host.contains("password=")) {
                url = String.format("%s&user=%s&password=%s", host, user, pass);
            }
        } else {
            // Construir la URL completa con SSL y credenciales
            url = String.format(
                "jdbc:postgresql://%s:%s/%s?sslmode=require&user=%s&password=%s",
                host, port, db, user, pass
            );
        }
        ctx.getLogger().log("[HealthCheckHandler] Checking DB with URL: " + url);

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("DB OK");
        }
    }
}