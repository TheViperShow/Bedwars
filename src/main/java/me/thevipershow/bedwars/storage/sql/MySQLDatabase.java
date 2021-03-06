package me.thevipershow.bedwars.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.config.DefaultConfiguration;
import me.thevipershow.bedwars.storage.sql.tables.DailyQuestsTableCreator;
import me.thevipershow.bedwars.storage.sql.tables.DataResetTableCreator;
import me.thevipershow.bedwars.storage.sql.tables.GlobalStatsTableCreator;
import me.thevipershow.bedwars.storage.sql.tables.WeeklyQuestsTableCreator;
import me.thevipershow.bedwars.storage.sql.tables.QueueVillagerTableCreator;
import me.thevipershow.bedwars.storage.sql.tables.RanksTableCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class MySQLDatabase extends Database {

    private final DefaultConfiguration defaultConfiguration;
    private static HikariDataSource dataSource = null;

    public MySQLDatabase(final JavaPlugin plugin, final DefaultConfiguration defaultConfiguration) {

        super(plugin,
                QueueVillagerTableCreator.class,
                RanksTableCreator.class,
                WeeklyQuestsTableCreator.class,
                DailyQuestsTableCreator.class,
                DataResetTableCreator.class,
                GlobalStatsTableCreator.class);

        this.defaultConfiguration = defaultConfiguration;
        final HikariDataSource dataSrc = new HikariDataSource();
        final String address = defaultConfiguration.getAddress();
        final int port = defaultConfiguration.getPort();
        final String dbName = defaultConfiguration.getDatabaseName();
        //final String jdbcUrl = "jdbc:mysql://" + address + ":" + port + "/" + dbName;
        final String jdbcUrl = String.format(AllStrings.JDBC_MYSQL_URL.get(), address, port, dbName);

        dataSrc.setJdbcUrl(jdbcUrl);
        dataSrc.setUsername(defaultConfiguration.getDatabaseUsername());
        dataSrc.setPassword(defaultConfiguration.getPassword());
        dataSrc.setDriverClassName(Bedwars.MYSQL_DRIVER_CLASS);
        dataSrc.setConnectionTimeout(500);
        dataSource = dataSrc;
        LoggerUtils.logColor(plugin.getLogger(), AllStrings.ATTEMPTING_CONNECTION.get() + jdbcUrl);
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
            } catch (final NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
