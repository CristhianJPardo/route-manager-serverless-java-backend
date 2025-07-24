package co.lacorporacionun.auth;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import co.lacorporacionun.util.DB;

public class AuthService {

    private final Connection connection;

    public AuthService() throws SQLException {
        this.connection = DB.getConnection();
    }

    public Optional<String> register(User user) {
        try {
            String hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt());

            String sql = "INSERT INTO managers (id, dni, username, firstname, lastname, email, password, phone, whatsapp, address, locality, neighborhood, latitude, longitude, role, hiring_date) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            UUID id = UUID.randomUUID();

            stmt.setObject(1, id);
            stmt.setString(2, user.dni);
            stmt.setString(3, user.username);
            stmt.setString(4, user.firstname);
            stmt.setString(5, user.lastname);
            stmt.setString(6, user.email);
            stmt.setString(7, hashedPassword);
            stmt.setString(8, user.phone);
            stmt.setString(9, user.whatsapp);
            stmt.setString(10, user.address);
            stmt.setString(11, user.locality);
            stmt.setString(12, user.neighborhood);
            stmt.setDouble(13, user.latitude);
            stmt.setDouble(14, user.longitude);
            stmt.setString(15, user.role);
            stmt.setDate(16, Date.valueOf(user.hiringDate));

            stmt.executeUpdate();

            String token = JWTUtil.generateToken(user.username);
            return Optional.of(token);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<String> login(String username, String password) {
        try {
            String sql = "SELECT password FROM managers WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    return Optional.of(JWTUtil.generateToken(username));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
