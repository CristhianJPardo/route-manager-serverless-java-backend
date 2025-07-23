package co.lacorporacionun.drivers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.time.LocalDate;

public class DriverService {
    private static final String JDBC_URL_BASE;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    private final ObjectMapper mapper;

    static {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String db   = System.getenv("DB_NAME");
        JDBC_URL_BASE = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        DB_USER = System.getenv("DB_USER");
        DB_PASSWORD = System.getenv("DB_PASSWORD");
        System.out.println("[DriverService] Initialized with base URL=" + JDBC_URL_BASE + " user=" + DB_USER);
    }

    public DriverService() {
        mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private Connection getConnection() throws SQLException {
        System.out.println("[DriverService] Opening DB connection with SSL");
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        props.setProperty("ssl", "true");
        props.setProperty("sslmode", "require");
        return DriverManager.getConnection(JDBC_URL_BASE, props);
    }

    public String listDrivers() {
        System.out.println("[DriverService] listDrivers()");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM drivers");
             ResultSet rs = ps.executeQuery()) {

            List<Driver> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            String json = mapper.writeValueAsString(list);
            System.out.println("[DriverService] Retrieved records=" + list.size());
            return json;
        } catch (Exception e) {
            System.err.println("[DriverService] listDrivers error: " + e);
            throw new RuntimeException(e);
        }
    }

    public String getDriverById(String id) {
        System.out.println("[DriverService] getDriverById id=" + id);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM drivers WHERE id=?")) {

            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String json = mapper.writeValueAsString(mapRow(rs));
                System.out.println("[DriverService] Found driver id=" + id);
                return json;
            } else {
                System.out.println("[DriverService] No driver for id=" + id);
                return "{}";
            }
        } catch (Exception e) {
            System.err.println("[DriverService] getDriverById error: " + e);
            throw new RuntimeException(e);
        }
    }

    public String createDriver(String body) {
        System.out.println("[DriverService] createDriver body=" + body);
        try (Connection conn = getConnection()) {
            Driver driver = mapper.readValue(body, Driver.class);
            String sql = "INSERT INTO drivers (firstname, lastname, dni, username, license_category, license_expiration, address, whatsapp, phone, email, password, locality, neighborhood, latitude, longitude, role) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, driver.getFirstname());
                ps.setString(2, driver.getLastname());
                ps.setString(3, driver.getDni());
                ps.setString(4, driver.getUsername());
                ps.setString(5, driver.getLicenseCategory());
                ps.setDate(6, Date.valueOf(driver.getLicenseExpiration()));
                ps.setString(7, driver.getAddress());
                ps.setString(8, driver.getWhatsapp());
                ps.setString(9, driver.getPhone());
                ps.setString(10, driver.getEmail());
                ps.setString(11, driver.getPassword());
                ps.setString(12, driver.getLocality());
                ps.setString(13, driver.getNeighborhood());
                ps.setDouble(14, driver.getLatitude());
                ps.setDouble(15, driver.getLongitude());
                ps.setString(16, driver.getRole());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String json = mapper.writeValueAsString(mapRow(rs));
                    System.out.println("[DriverService] Created driver id=" + rs.getInt("id"));
                    return json;
                }
                throw new SQLException("Creation failed, no row returned");
            }
        } catch (Exception e) {
            System.err.println("[DriverService] createDriver error: " + e);
            throw new RuntimeException(e);
        }
    }

    public String updateDriver(String id, String body) {
        System.out.println("[DriverService] updateDriver id=" + id + " body=" + body);
        try (Connection conn = getConnection()) {
            Driver driver = mapper.readValue(body, Driver.class);
            String sql = "UPDATE drivers SET firstname=?, lastname=?, dni=?, username=?, license_category=?, license_expiration=?, address=?, whatsapp=?, phone=?, email=?, password=?, locality=?, neighborhood=?, latitude=?, longitude=?, role=? WHERE id=? RETURNING *";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, driver.getFirstname());
                ps.setString(2, driver.getLastname());
                ps.setString(3, driver.getDni());
                ps.setString(4, driver.getUsername());
                ps.setString(5, driver.getLicenseCategory());
                ps.setDate(6, Date.valueOf(driver.getLicenseExpiration()));
                ps.setString(7, driver.getAddress());
                ps.setString(8, driver.getWhatsapp());
                ps.setString(9, driver.getPhone());
                ps.setString(10, driver.getEmail());
                ps.setString(11, driver.getPassword());
                ps.setString(12, driver.getLocality());
                ps.setString(13, driver.getNeighborhood());
                ps.setDouble(14, driver.getLatitude());
                ps.setDouble(15, driver.getLongitude());
                ps.setString(16, driver.getRole());
                ps.setInt(17, Integer.parseInt(id));

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String json = mapper.writeValueAsString(mapRow(rs));
                    System.out.println("[DriverService] Updated driver id=" + id);
                    return json;
                }
                return "{}";
            }
        } catch (Exception e) {
            System.err.println("[DriverService] updateDriver error: " + e);
            throw new RuntimeException(e);
        }
    }

    public void deleteDriver(String id) {
        System.out.println("[DriverService] deleteDriver id=" + id);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM drivers WHERE id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            int rows = ps.executeUpdate();
            System.out.println("[DriverService] deleteDriver affectedRows=" + rows);
        } catch (Exception e) {
            System.err.println("[DriverService] deleteDriver error: " + e);
            throw new RuntimeException(e);
        }
    }

    private Driver mapRow(ResultSet rs) throws SQLException {
        Driver d = new Driver();
        d.setId(rs.getInt("id"));
        d.setFirstname(rs.getString("firstname"));
        d.setLastname(rs.getString("lastname"));
        d.setDni(rs.getString("dni"));
        d.setUsername(rs.getString("username"));
        d.setLicenseCategory(rs.getString("license_category"));
        d.setLicenseExpiration(rs.getDate("license_expiration").toLocalDate());
        d.setAddress(rs.getString("address"));
        d.setWhatsapp(rs.getString("whatsapp"));
        d.setPhone(rs.getString("phone"));
        d.setEmail(rs.getString("email"));
        d.setPassword(rs.getString("password"));
        d.setLocality(rs.getString("locality"));
        d.setNeighborhood(rs.getString("neighborhood"));
        d.setLatitude(rs.getDouble("latitude"));
        d.setLongitude(rs.getDouble("longitude"));
        d.setRole(rs.getString("role"));
        d.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        d.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        return d;
    }
}
