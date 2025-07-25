package io.quarkus.ts.spring.web.reactive;

public class MariaDBConstants {
    public static final int PORT = 3306;
    public static final String START_LOG = "Only MySQL server logs after this point";
    public static final String IMAGE_1011 = "${mariadb.1011.image}";
    public static final String START_LOG_11 = "socket: '.*/mysql.*sock'  port: " + PORT;
    public static final String IMAGE_11 = "${mariadb.11.image}";
}
