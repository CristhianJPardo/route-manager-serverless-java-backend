// modules/clients/src/main/java/co/lacorporacionun/clients/ClientsHandler.java
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
        String path = req.getPath();
        Map<String, String> pathParams = req.getPathParameters();

        log.log("[ClientsHandler] Received request: method=" + method + ", path=" + path);
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
                    response = new APIGatewayProxyResponseEvent().withStatusCode(204);
                    break;
                default:
                    response = new APIGatewayProxyResponseEvent().withStatusCode(405).withBody("{\"error\":\"Method Not Allowed\"}");
            }
            log.log("[ClientsHandler] Responding with status=" + response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.log("[ClientsHandler] Exception: " + e.getMessage());
            for (var ste : e.getStackTrace()) {
                log.log(ste.toString());
            }
            return new APIGatewayProxyResponseEvent().withStatusCode(500)
                .withBody("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent buildResponse(int code, String body) {
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(code)
            .withHeaders(Map.of("Content-Type", "application/json"))
            .withBody(body);
    }
}
