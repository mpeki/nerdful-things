package dk.pekilidi.db;

import dk.pekilidi.exceptions.CannotReadPropertiesException;
import dk.pekilidi.exceptions.ConnectionException;
import dk.pekilidi.properties.PropertyUtil;
import dk.pekilidi.ssh.SshTunnelManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class DatabaseManager {

    private final Map<String, SessionFactory> sessionFactories = new java.util.HashMap<>();
    private final List<File> dbPpropertiesList;
    private final File sshPropertiesFile;

    public DatabaseManager(File sshPropertiesFile, List<File> dbPropertyFiles, List<Class<? extends NamedNativeQueryEntity>> queries) {
        this.dbPpropertiesList = dbPropertyFiles;
        this.sshPropertiesFile = sshPropertiesFile;
        dbPpropertiesList.forEach(dbProperties ->
                sessionFactories.put(dbProperties.getName().replaceFirst("[.][^.]+$", ""), buildSessionFactory(dbProperties, queries)));
    }



    public DatabaseManager(List<File> dbPropertyFiles, List<Class<? extends NamedNativeQueryEntity>> queries) {
        this(null, dbPropertyFiles, queries);
    }



    private SessionFactory buildSessionFactory(File dbProperties, List<Class<? extends NamedNativeQueryEntity>> queries) {
        Properties dbProps = null;
        try {
            dbProps = PropertyUtil.loadFromFile(dbProperties);
        } catch (IOException e) {
            throw new CannotReadPropertiesException(e);
        }

        String dbDriver = dbProps.getProperty("db.driver", "org.postgresql.Driver");
        String dbHost = dbProps.getProperty("db.host", "localhost");
        String dbPort = dbProps.getProperty("db.port", "5432");
        String dbName = dbProps.getProperty("db.name", "");
        String dbUser = dbProps.getProperty("db.user", "");
        String dbPass = dbProps.getProperty("db.password", "");

        // If you have a single DB that needs tunneling, you can do it inline:
        if(sshPropertiesFile != null) {
            try {
                SshTunnelManager tunnelManager = new SshTunnelManager(sshPropertiesFile);
                tunnelManager.connectAndForwardPort();
                // Now override the DB host/port so that the JDBC connects to localhost:<localPort>
                dbHost = "127.0.0.1";
                dbPort = String.valueOf("54321");

            } catch (Exception e) {
                throw new ConnectionException("Failed to establish SSH tunnel", e);
            }
        }



        String jdbcUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        Properties hibProps = new Properties();
        hibProps.putAll(dbProps);

        hibProps.setProperty("hibernate.connection.driver_class", dbDriver);
        hibProps.setProperty("hibernate.connection.url", jdbcUrl);
        hibProps.setProperty("hibernate.connection.username", dbUser);
        hibProps.setProperty("hibernate.connection.password", dbPass);

        Configuration configuration = new Configuration();
        configuration.setProperties(hibProps);
        configuration.addAnnotatedClass(NamedNativeQueryEntity.class);
        queries.forEach(configuration::addAnnotatedClass);

        return configuration.buildSessionFactory();
    }

    private Session openSession(String databaseName) {
        return sessionFactories.get(databaseName).openSession();
    }

    public void close() {
        sessionFactories.values().forEach(SessionFactory::close);
    }

    public <T extends NamedNativeQueryEntity> List<T> executeNamedNativeQuery(
            BaseName databaseName,
            Class<T> namedQueryEntityClass,
            String queryName,
            Map<String, String> queryParams
    ) {
        Session session = openSession(databaseName.getName());
        List<T> results = Collections.emptyList();

        try {
            Transaction tx = session.beginTransaction();

            // The type of the query is T here, preserving the more specific type.
            Query<T> nativeQuery = session.createNamedQuery(queryName, namedQueryEntityClass);

            if (!queryParams.isEmpty()) {
                queryParams.forEach(nativeQuery::setParameter);
            }

            log.info("Executing query: {}", queryName);

            results = nativeQuery.list();
            tx.commit();
        } finally {
            session.close();
            close();
        }

        return results;
    }
}
