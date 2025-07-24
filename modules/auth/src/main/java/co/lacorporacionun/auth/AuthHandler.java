package co.lacorporacionun.auth;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.lacorporacionun.util.*;

import java.util.Map;

public class AuthHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthHandler() {
        try {
            this.authService = new AuthService();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AuthService", e);
        }
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String httpMethod = (String) input.get("httpMethod");
        String path = (String) input.get("path");

        switch (httpMethod) {
            case "POST":
                if (path.endsWith("/auth/register")) return handleRegister(input);
                if (path.endsWith("/auth/login")) return handleLogin(input);
                break;
            case "OPTIONS":
                return ResponseUtil.corsResponse();
        }

        return ResponseUtil.badRequest("Invalid request");
    }

    private ApiGatewayResponse handleRegister(Map<String, Object> input) {
        try {
            String body = (String) input.get("body");
            User user = objectMapper.readValue(body, User.class);
            return authService.register(user)
                    .map(token -> ResponseUtil.ok(Map.of("token", token, "manager", user)))
                    .orElse(ResponseUtil.internalError("Error during registration"));
        } catch (Exception e) {
            return ResponseUtil.internalError("Exception: " + e.getMessage());
        }
    }

    private ApiGatewayResponse handleLogin(Map<String, Object> input) {
        try {
            String body = (String) input.get("body");
            Map<String, String> payload = objectMapper.readValue(body, Map.class);
            String username = payload.get("username");
            String password = payload.get("password");

            return authService.login(username, password)
                    .map(token -> ResponseUtil.ok(Map.of("token", token)))
                    .orElse(ResponseUtil.unauthorized("Invalid credentials"));
        } catch (Exception e) {
            return ResponseUtil.internalError("Exception: " + e.getMessage());
        }
    }
}
