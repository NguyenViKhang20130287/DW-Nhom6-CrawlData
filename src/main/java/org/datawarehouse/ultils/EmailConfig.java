package org.datawarehouse.ultils;

public class EmailConfig {
    public static final String HOST_NAME = "smtp.gmail.com";
    public static final int TSL_PORT = 587; // Port for TLS/STARTTLS
    public static final String APP_EMAIL = "vikhang17112002@gmail.com"; // your email
    public static final String APP_PASSWORD = "fzjljqjsfkltospu"; // your password
    public static final String TO_EMAIL = new PropertiesReader().getProperty("to.email");;
}
