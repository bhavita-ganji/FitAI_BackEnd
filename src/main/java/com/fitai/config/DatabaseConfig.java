package com.fitai.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Dynamic DataSource Configuration.
 * Automatically parses and configures:
 * 1. Render PostgreSQL connection strings (DATABASE_URL / INTERNAL_DATABASE_URL).
 * 2. Standard Spring JDBC URLs (SPRING_DATASOURCE_URL).
 * 3. Fallback H2 / MySQL for local development and integration tests.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        // Priority 1: Check Render's standard DATABASE_URL or INTERNAL_DATABASE_URL
        String rawDbUrl = env.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(rawDbUrl)) {
            rawDbUrl = env.getProperty("INTERNAL_DATABASE_URL");
        }
        if (!StringUtils.hasText(rawDbUrl)) {
            rawDbUrl = env.getProperty("SPRING_DATASOURCE_URL");
        }

        // Priority 2: Check standard Render / Postgres PG* environment variables if URL wasn't provided
        if (!StringUtils.hasText(rawDbUrl) && StringUtils.hasText(env.getProperty("PGHOST"))) {
            logger.info("Found Render PG* environment variables. Constructing PostgreSQL DataSource...");
            return createFromPgEnvironmentVariables();
        }

        // Priority 3: Parse provided database URL
        if (StringUtils.hasText(rawDbUrl)) {
            String trimmedUrl = rawDbUrl.trim();
            if (trimmedUrl.startsWith("postgres://") || trimmedUrl.startsWith("postgresql://")) {
                logger.info("Detected PostgreSQL URL format from Render. Parsing connection parameters...");
                return createPostgresDataSourceFromUri(trimmedUrl);
            } else if (trimmedUrl.startsWith("jdbc:")) {
                logger.info("Using configured JDBC URL: {}", maskUrl(trimmedUrl));
                return createHikariDataSource(
                        trimmedUrl,
                        env.getProperty("spring.datasource.username", env.getProperty("SPRING_DATASOURCE_USERNAME", "")),
                        env.getProperty("spring.datasource.password", env.getProperty("SPRING_DATASOURCE_PASSWORD", "")),
                        env.getProperty("spring.datasource.driver-class-name", detectDriver(trimmedUrl))
                );
            }
        }

        // Priority 4: Default fallback from Spring properties (H2 for local dev/testing)
        String fallbackUrl = env.getProperty("spring.datasource.url", "jdbc:h2:mem:fitai_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL");
        String fallbackUser = env.getProperty("spring.datasource.username", "sa");
        String fallbackPass = env.getProperty("spring.datasource.password", "");
        String fallbackDriver = env.getProperty("spring.datasource.driver-class-name", detectDriver(fallbackUrl));

        logger.info("Using default/fallback DataSource: {}", maskUrl(fallbackUrl));
        return createHikariDataSource(fallbackUrl, fallbackUser, fallbackPass, fallbackDriver);
    }

    /**
     * Parses a postgres:// or postgresql:// URI into a JDBC DataSource.
     */
    private DataSource createPostgresDataSourceFromUri(String rawUri) {
        try {
            // Standardize scheme for URI parser
            String parseableUri = rawUri;
            if (parseableUri.startsWith("postgres://")) {
                parseableUri = "postgresql://" + parseableUri.substring("postgres://".length());
            }

            URI uri = new URI(parseableUri);

            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String dbName = (path != null && path.length() > 1) ? path.substring(1) : "fitai_db";

            String username = null;
            String password = null;
            if (uri.getUserInfo() != null) {
                String[] userParts = uri.getUserInfo().split(":", 2);
                username = URLDecoder.decode(userParts[0], StandardCharsets.UTF_8);
                if (userParts.length > 1) {
                    password = URLDecoder.decode(userParts[1], StandardCharsets.UTF_8);
                }
            }

            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                    .append(host)
                    .append(":")
                    .append(port)
                    .append("/")
                    .append(dbName);

            String query = uri.getQuery();
            if (StringUtils.hasText(query)) {
                jdbcUrl.append("?").append(query);
            } else if (host != null && host.contains("render.com")) {
                // External Render Postgres requires SSL mode
                jdbcUrl.append("?sslmode=require");
            }

            logger.info("Successfully parsed Render PostgreSQL connection string: {}", maskUrl(jdbcUrl.toString()));
            return createHikariDataSource(jdbcUrl.toString(), username, password, "org.postgresql.Driver");
        } catch (Exception ex) {
            logger.error("Failed to parse PostgreSQL URI: {}. Falling back to default URL config.", maskUrl(rawUri), ex);
            throw new IllegalStateException("Invalid database URI format: " + maskUrl(rawUri), ex);
        }
    }

    /**
     * Creates DataSource from individual PG* environment variables if supplied.
     */
    private DataSource createFromPgEnvironmentVariables() {
        String host = env.getProperty("PGHOST", "localhost");
        String port = env.getProperty("PGPORT", "5432");
        String dbName = env.getProperty("PGDATABASE", "fitai_db");
        String user = env.getProperty("PGUSER", "postgres");
        String pass = env.getProperty("PGPASSWORD", "");

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(host).append(":").append(port).append("/").append(dbName);

        if (host.contains("render.com")) {
            jdbcUrl.append("?sslmode=require");
        }

        return createHikariDataSource(jdbcUrl.toString(), user, pass, "org.postgresql.Driver");
    }

    /**
     * Instantiates HikariDataSource configured with connection pooling optimized for cloud hosting.
     */
    private DataSource createHikariDataSource(String jdbcUrl, String username, String password, String driverClassName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);

        if (StringUtils.hasText(username)) {
            config.setUsername(username);
        }
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }
        if (StringUtils.hasText(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }

        // Connection pool optimization for Render free / starter tier
        int maxPoolSize = Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size", "10"));
        int minIdle = Integer.parseInt(env.getProperty("spring.datasource.hikari.minimum-idle", "2"));
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("FitAiHikariPool");

        return new HikariDataSource(config);
    }

    /**
     * Detects JDBC driver class name based on URL scheme.
     */
    private String detectDriver(String url) {
        if (!StringUtils.hasText(url)) return "org.h2.Driver";
        if (url.startsWith("jdbc:postgresql:")) return "org.postgresql.Driver";
        if (url.startsWith("jdbc:mysql:")) return "com.mysql.cj.jdbc.Driver";
        if (url.startsWith("jdbc:h2:")) return "org.h2.Driver";
        return "org.postgresql.Driver";
    }

    /**
     * Masks credentials in logged database URLs for security.
     */
    private String maskUrl(String url) {
        if (!StringUtils.hasText(url)) return "";
        return url.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }
}
