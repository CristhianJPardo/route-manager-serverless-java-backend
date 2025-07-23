// modules/clients/src/main/java/co/lacorporacionun/clients/Client.java
package co.lacorporacionun.clients;

import java.time.Instant;
import java.time.LocalDate;

public class Client {
    private int id;
    private String firstname;
    private String lastname;
    private String dni;
    private String username;
    private String address;
    private String whatsapp;
    private String phone;
    private String email;
    private String password;
    private String locality;
    private String neighborhood;
    private double latitude;
    private double longitude;
    private String role;
    private LocalDate registrationDate;
    private int precedence;
    private boolean enabled;
    private boolean credentialsNonExpired;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private Instant createdAt;
    private Instant updatedAt;

    // Getters and setters omitted for brevity...
}