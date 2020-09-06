package me.thevipershow.aussiebedwars.storage.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.config.DefaultConfiguration;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueVillagerTableCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class MySQLDatabase extends Database {
    private final DefaultConfiguration defaultConfiguration;
    private final HikariConfig hikariConfig;
    private static HikariDataSource dataSource = null;

    public MySQLDatabase(JavaPlugin plugin,
                         DefaultConfiguration defaultConfiguration) {
        super(plugin, QueueVillagerTableCreator.class);
        this.defaultConfiguration = defaultConfiguration;
        hikariConfig = new HikariConfig();
        hikariConfig.setUsername(defaultConfiguration.getDatabaseUsername());
        hikariConfig.setPassword(defaultConfiguration.getPassword());
        hikariConfig.setDriverClassName(AussieBedwars.MYSQL_DRIVER_CLASS.getCanonicalName());
        hikariConfig.setConnectionTimeout(3000);
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
                defaultConfiguration.getAddress(),
                defaultConfiguration.getPort(),
                defaultConfiguration.getDatabaseName()));
        dataSource = new HikariDataSource(this.hikariConfig);
    }

    public static Optional<Connection> getConnection() {
        try {
            final Connection connection = dataSource.getConnection();
            return connection != null ? Optional.of(connection) : Optional.empty();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void createTables() {
        for (final Class<? extends TableCreator> tableCreator : tableCreators) {
            try {
                final Constructor<? extends TableCreator> constructor = tableCreator.getConstructor(Connection.class);
                final Optional<Connection> connection = getConnection();
                if (connection.isPresent()) {
                    constructor.newInstance(connection.get()).createTable();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
