// modules/health/src/main/java/co/lacorporacionun/health/HealthCheckHandler.java
package co.lacorporacionun.health;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class HealthCheckHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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
        // Build JDBC URL from environment
        String hostEnv = System.getenv("DB_HOST");
        String url;
        if (hostEnv.startsWith("jdbc:")) {
            url = hostEnv;
        } else {
            String port = System.getenv("DB_PORT");
            String db   = System.getenv("DB_NAME");
            // ensure SSL
            url = String.format(
                "jdbc:postgresql://%s:%s/%s?sslmode=require",
                hostEnv, port, db
            );
        }
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");
        ctx.getLogger().log("[HealthCheckHandler] Checking DB with URL: " + url);

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("DB OK");
        }
    }
}