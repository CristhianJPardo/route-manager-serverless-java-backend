// modules/clients/src/main/java/co/lacorporacionun/clients/ClientService.java
package co.lacorporacionun.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClientService {
    private static final String JDBC_URL_BASE;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    private final ObjectMapper mapper;

    static {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String name = System.getenv("DB_NAME");
        JDBC_URL_BASE = String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
        DB_USER = System.getenv("DB_USER");
        DB_PASSWORD = System.getenv("DB_PASSWORD");
        System.out.println("[ClientService] Initialized with base URL=" + JDBC_URL_BASE + " user=" + DB_USER);
    }

    public ClientService() {
        mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private Connection getConnection() throws SQLException {
        System.out.println("[ClientService] Opening DB connection with SSL");
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        props.setProperty("ssl", "true");
        props.setProperty("sslmode", "require");
        return DriverManager.getConnection(JDBC_URL_BASE, props);
    }

    public String listClients() {
        System.out.println("[ClientService] listClients()");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients");
             ResultSet rs = ps.executeQuery()) {
            List<Client> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            String json = mapper.writeValueAsString(list);
            System.out.println("[ClientService] Retrieved records=" + list.size());
            return json;
        } catch (Exception e) {
            System.err.println("[ClientService] listClients error: " + e);
            throw new RuntimeException(e);
        }
    }

    public String getClientById(String id) {
        System.out.println("[ClientService] getClientById id=" + id);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients WHERE id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String json = mapper.writeValueAsString(mapRow(rs));
                System.out.println("[ClientService] Found client id=" + id);
                return json;
            } else {
                System.out.println("[ClientService] No client for id=" + id);
                return "{}";
            }
        } catch (Exception e) {
            System.err.println("[ClientService] getClientById error: " + e);
            throw new RuntimeException(e);
        }
    }

    public String createClient(String body) {
        System.out.println("[ClientService] createClient body=" + body);
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
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String json = mapper.writeValueAsString(mapRow(rs));
                    System.out.println("[ClientService] Created client id=" + rs.getInt("id"));
                    return json;
                }
                throw new SQLException("Creation failed, no row returned");
            }
        } catch (Exception e) {
            System.err.println("[ClientService] createClient error: " + e);
            throw new RuntimeException(e);
        }
    }

    public String updateClient(String id, String body) {
        System.out.println("[ClientService] updateClient id=" + id + " body=" + body);
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
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String json = mapper.writeValueAsString(mapRow(rs));
                    System.out.println("[ClientService] Updated client id=" + id);
                    return json;
                }
                return "{}";
            }
        } catch (Exception e) {
            System.err.println("[ClientService] updateClient error: " + e);
            throw new RuntimeException(e);
        }
    }

    public void deleteClient(String id) {
        System.out.println("[ClientService] deleteClient id=" + id);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM clients WHERE id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            int rows = ps.executeUpdate();
            System.out.println("[ClientService] deleteClient affectedRows=" + rows);
        } catch (Exception e) {
            System.err.println("[ClientService] deleteClient error: " + e);
            throw new RuntimeException(e);
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