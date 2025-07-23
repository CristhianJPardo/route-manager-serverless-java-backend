// modules/clients/src/main/java/co/lacorporacionun/clients/ClientService.java
package co.lacorporacionun.clients;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private static final String JDBC_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    private final ObjectMapper mapper;

    static {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String name = System.getenv("DB_NAME");
        JDBC_URL = String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
        DB_USER = System.getenv("DB_USER");
        DB_PASSWORD = System.getenv("DB_PASSWORD");
        System.out.println("[ClientService] Initialized with URL=" + JDBC_URL + " user=" + DB_USER);
    }

    public ClientService() {
        mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private Connection getConnection() throws SQLException {
        System.out.println("[ClientService] Opening DB connection");
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(JDBC_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        return ds.getConnection();
    }

    public String listClients() {
        System.out.println("[ClientService] listClients() called");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients");
             ResultSet rs = ps.executeQuery()) {

            List<Client> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            String json = mapper.writeValueAsString(list);
            System.out.println("[ClientService] listClients() returned " + list.size() + " records");
            return json;
        } catch (Exception e) {
            System.err.println("[ClientService] Error in listClients: " + e.getMessage());
            throw new RuntimeException("Error listing clients", e);
        }
    }

    public String getClientById(String id) {
        System.out.println("[ClientService] getClientById(" + id + ") called");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients WHERE id = ?")) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = mapper.writeValueAsString(mapRow(rs));
                    System.out.println("[ClientService] getClientById(" + id + ") found record");
                    return json;
                } else {
                    System.out.println("[ClientService] getClientById(" + id + ") no record");
                    return "{}";
                }
            }
        } catch (Exception e) {
            System.err.println("[ClientService] Error in getClientById: " + e.getMessage());
            throw new RuntimeException("Error fetching client by id", e);
        }
    }

    public String createClient(String body) {
        System.out.println("[ClientService] createClient() called with body=" + body);
        try (Connection conn = getConnection()) {
            Client client = mapper.readValue(body, Client.class);
            String sql = "INSERT INTO clients (firstname, lastname, dni, username, address, whatsapp, phone, email, password, locality, neighborhood, latitude, longitude, role, registration_date, precedence, enabled, credentials_non_expired, account_non_expired, account_non_locked) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, client.getFirstname());
                ps.setString(2, client.getLastname());
                ps.setString(3, client.getDni());
                ps.setString(4, client.getUsername());
                ps.setString(5, client.getAddress());
                ps.setString(6, client.getWhatsapp());
                ps.setString(7, client.getPhone());
                ps.setString(8, client.getEmail());
                ps.setString(9, client.getPassword());
                ps.setString(10, client.getLocality());
                ps.setString(11, client.getNeighborhood());
                ps.setDouble(12, client.getLatitude());
                ps.setDouble(13, client.getLongitude());
                ps.setString(14, client.getRole());
                ps.setDate(15, Date.valueOf(client.getRegistrationDate()));
                ps.setInt(16, client.getPrecedence());
                ps.setBoolean(17, client.isEnabled());
                ps.setBoolean(18, client.isCredentialsNonExpired());
                ps.setBoolean(19, client.isAccountNonExpired());
                ps.setBoolean(20, client.isAccountNonLocked());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String json = mapper.writeValueAsString(mapRow(rs));
                        System.out.println("[ClientService] createClient() succeeded id=" + rs.getInt("id"));
                        return json;
                    } else {
                        throw new SQLException("Insert returned no rows");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ClientService] Error in createClient: " + e.getMessage());
            throw new RuntimeException("Error creating client", e);
        }
    }

    // updateClient and deleteClient similar logging as above...

    // ... remaining methods unchanged ...

    private Client mapRow(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getInt("id"));
        // ... mapping fields ...
        return c;
    }
}