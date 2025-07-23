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
        String password = System.getenv("DB_PASSWORD");

        String jdbcUrl = String.format(
            "jdbc:postgresql://aws-0-us-east-2.pooler.supabase.com:6543/postgres?user=postgres.rjfcrbysxgylfjtyluor&password=%s",
            password
        );

        ctx.getLogger().log("[HealthCheckHandler] Checking DB using pooled URL.");

        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("DB OK");
        }
    }
}
