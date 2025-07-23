package co.lacorporacionun.drivers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class DriversHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DriverService service = new DriverService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent req, Context ctx) {
        String method = req.getHttpMethod();
        String path = req.getPath();
        String id = req.getPathParameters() != null ? req.getPathParameters().get("id") : null;

        try {
            return switch (method) {
                case "GET" -> (id != null) ?
                        response(200, service.getDriverById(id)) :
                        response(200, service.listDrivers());
                case "POST" -> response(201, service.createDriver(req.getBody()));
                case "PUT" -> response(200, service.updateDriver(id, req.getBody()));
                case "DELETE" -> {
                    service.deleteDriver(id);
                    yield response(204, "");
                }
                default -> response(405, "Method Not Allowed");
            };
        } catch (Exception e) {
            ctx.getLogger().log("[DriversHandler] Error: " + e);
            return response(500, "ERROR: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent response(int status, String body) {
        return new APIGatewayProxyResponseEvent().withStatusCode(status).withBody(body);
    }
}
