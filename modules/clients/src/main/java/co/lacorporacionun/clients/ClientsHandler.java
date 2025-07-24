package co.lacorporacionun.clients;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;

public class ClientsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ClientService service = new ClientService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent req, Context ctx) {
        var log = ctx.getLogger();
        String method = req.getHttpMethod();
        String path = req.getResource();
        Map<String, String> pathParams = req.getPathParameters();

        log.log("[ClientsHandler] Received request method=" + method + ", resource=" + path);
        try {
            APIGatewayProxyResponseEvent response;
            switch (method) {
                case "GET":
                    if (pathParams != null && pathParams.containsKey("id")) {
                        response = buildResponse(200, service.getClientById(pathParams.get("id")));
                    } else {
                        response = buildResponse(200, service.listClients());
                    }
                    break;
                case "POST":
                    response = buildResponse(201, service.createClient(req.getBody()));
                    break;
                case "PUT":
                    response = buildResponse(200, service.updateClient(pathParams.get("id"), req.getBody()));
                    break;
                case "DELETE":
                    service.deleteClient(pathParams.get("id"));
                    response = new APIGatewayProxyResponseEvent()
                            .withStatusCode(204)
                            .withHeaders(corsHeaders());
                    break;
                case "OPTIONS":
                    response = new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(corsHeaders());
                    break;
                default:
                    response = new APIGatewayProxyResponseEvent()
                            .withStatusCode(405)
                            .withHeaders(corsHeaders())
                            .withBody("{\"error\":\"Method Not Allowed\"}");
            }
            log.log("[ClientsHandler] Responding status=" + response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.log("[ClientsHandler] Exception: " + e);
            for (var ste : e.getStackTrace()) log.log(ste.toString());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(corsHeaders())
                    .withBody("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent buildResponse(int code, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(code)
                .withHeaders(corsHeaders())
                .withBody(body);
    }

    private Map<String, String> corsHeaders() {
        return Map.of(
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Methods", "OPTIONS,GET,POST,PUT,DELETE",
                "Access-Control-Allow-Headers", "Content-Type,Authorization",
                "Content-Type", "application/json"
        );
    }
}
