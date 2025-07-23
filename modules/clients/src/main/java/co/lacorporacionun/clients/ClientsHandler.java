package co.lacorporacionun.clients;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.*;

import java.util.Map;

public class ClientsHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ClientService service = new ClientService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent req,
            Context ctx) {

        String method = req.getHttpMethod();
        Map<String,String> pathParams = req.getPathParameters();

        try {
            switch (method) {
                case "GET":
                    if (pathParams != null && pathParams.containsKey("id")) {
                        return buildResponse(200, service.getClientById(pathParams.get("id")));
                    } else {
                        return buildResponse(200, service.listClients());
                    }
                case "POST":
                    return buildResponse(201, service.createClient(req.getBody()));
                case "PUT":
                    return buildResponse(200, service.updateClient(pathParams.get("id"), req.getBody()));
                case "DELETE":
                    service.deleteClient(pathParams.get("id"));
                    return new APIGatewayProxyResponseEvent().withStatusCode(204);
                default:
                    return new APIGatewayProxyResponseEvent()
                        .withStatusCode(405)
                        .withBody("{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withBody("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent buildResponse(int code, String body) {
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(code)
            .withHeaders(Map.of("Content-Type","application/json"))
            .withBody(body);
    }
}
