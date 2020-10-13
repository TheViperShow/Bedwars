package me.thevipershow.aussiebedwars.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.LoggerUtils;
import me.thevipershow.aussiebedwars.config.DefaultConfiguration;
import me.thevipershow.aussiebedwars.storage.sql.queue.WeeklyQuestsTableCreator;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueVillagerTableCreator;
import me.thevipershow.aussiebedwars.storage.sql.queue.RanksTableCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class MySQLDatabase extends Database {

    private final DefaultConfiguration defaultConfiguration;
    private static HikariDataSource dataSource = null;

    public MySQLDatabase(final JavaPlugin plugin, final DefaultConfiguration defaultConfiguration) {

        super(plugin, QueueVillagerTableCreator.class, RanksTableCreator.class, WeeklyQuestsTableCreator.class);

        this.defaultConfiguration = defaultConfiguration;
        HikariDataSource dataSrc = new HikariDataSource();
        final String address = defaultConfiguration.getAddress();
        final int port = defaultConfiguration.getPort();
        final String dbName = defaultConfiguration.getDatabaseName();
        final String jdbcUrl = "jdbc:mysql://" + address + ":" + port + "/" + dbName;

        dataSrc.setJdbcUrl(jdbcUrl);
        dataSrc.setUsername(defaultConfiguration.getDatabaseUsername());
        dataSrc.setPassword(defaultConfiguration.getPassword());
        dataSrc.setDriverClassName(AussieBedwars.MYSQL_DRIVER_CLASS);
        dataSrc.setConnectionTimeout(3500);
        dataSource = dataSrc;
        LoggerUtils.logColor(plugin.getLogger(), "&eAttempting MySQL Connection to address ->&a" + jdbcUrl);
        createTables();
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
    public final void createTables() {
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
