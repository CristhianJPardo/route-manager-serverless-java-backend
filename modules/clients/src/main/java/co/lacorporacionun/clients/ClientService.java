// modules/clients/src/main/java/co/lacorporacionun/clients/ClientService.java
package co.lacorporacionun.clients;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    }

    public ClientService() {
        mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private Connection getConnection() throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(JDBC_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        return ds.getConnection();
    }

    public String listClients() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients");
             ResultSet rs = ps.executeQuery()) {

            List<Client> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Error listing clients", e);
        }
    }

    public String getClientById(String id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients WHERE id = ?")) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.writeValueAsString(mapRow(rs));
                } else {
                    return "{}";
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching client by id", e);
        }
    }

    public String createClient(String body) {
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
                        return mapper.writeValueAsString(mapRow(rs));
                    } else {
                        throw new SQLException("Insert returned no rows");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating client", e);
        }
    }

    public String updateClient(String id, String body) {
        try (Connection conn = getConnection()) {
            Client client = mapper.readValue(body, Client.class);
            String sql = "UPDATE clients SET firstname=?, lastname=?, address=?, whatsapp=?, phone=?, email=?, locality=?, neighborhood=?, latitude=?, longitude=?, role=?, precedence=?, enabled=?, credentials_non_expired=?, account_non_expired=?, account_non_locked=? WHERE id=? RETURNING *";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, client.getFirstname());
                ps.setString(2, client.getLastname());
                ps.setString(3, client.getAddress());
                ps.setString(4, client.getWhatsapp());
                ps.setString(5, client.getPhone());
                ps.setString(6, client.getEmail());
                ps.setString(7, client.getLocality());
                ps.setString(8, client.getNeighborhood());
                ps.setDouble(9, client.getLatitude());
                ps.setDouble(10, client.getLongitude());
                ps.setString(11, client.getRole());
                ps.setInt(12, client.getPrecedence());
                ps.setBoolean(13, client.isEnabled());
                ps.setBoolean(14, client.isCredentialsNonExpired());
                ps.setBoolean(15, client.isAccountNonExpired());
                ps.setBoolean(16, client.isAccountNonLocked());
                ps.setInt(17, Integer.parseInt(id));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapper.writeValueAsString(mapRow(rs));
                    } else {
                        return "{}";
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating client", e);
        }
    }

    public void deleteClient(String id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting client", e);
        }
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getInt("id"));
        c.setFirstname(rs.getString("firstname"));
        c.setLastname(rs.getString("lastname"));
        c.setDni(rs.getString("dni"));
        c.setUsername(rs.getString("username"));
        c.setAddress(rs.getString("address"));
        c.setWhatsapp(rs.getString("whatsapp"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setPassword(rs.getString("password"));
        c.setLocality(rs.getString("locality"));
        c.setNeighborhood(rs.getString("neighborhood"));
        c.setLatitude(rs.getDouble("latitude"));
        c.setLongitude(rs.getDouble("longitude"));
        c.setRole(rs.getString("role"));
        c.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        c.setPrecedence(rs.getInt("precedence"));
        c.setEnabled(rs.getBoolean("enabled"));
        c.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        c.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        c.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        c.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        c.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        return c;
    }
}
