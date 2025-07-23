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
        String id = null;
        if (req.getPathParameters() != null) {
            id = req.getPathParameters().get("id");
        }

        try {
            switch (method) {
                case "GET":
                    if (id != null) {
                        return response(200, service.getDriverById(id));
                    } else {
                        return response(200, service.listDrivers());
                    }
                case "POST":
                    return response(201, service.createDriver(req.getBody()));
                case "PUT":
                    return response(200, service.updateDriver(id, req.getBody()));
                case "DELETE":
                    service.deleteDriver(id);
                    return response(204, "");
                default:
                    return response(405, "Method Not Allowed");
            }
        } catch (Exception e) {
            ctx.getLogger().log("[DriversHandler] Error: " + e);
            return response(500, "ERROR: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent response(int status, String body) {
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(status)
            .withBody(body);
    }
}
