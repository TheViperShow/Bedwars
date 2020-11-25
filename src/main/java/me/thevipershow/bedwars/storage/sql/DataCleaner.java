package me.thevipershow.bedwars.storage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.storage.sql.tables.DailyQuestsTableCreator;
import me.thevipershow.bedwars.storage.sql.tables.WeeklyQuestsTableCreator;
import org.bukkit.plugin.Plugin;

public final class DataCleaner {

    private final Plugin plugin;

    public DataCleaner(final Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String GET_EVENTSCHEDULER_STATUS = "select count(*) from (select * from information_schema.PROCESSLIST where USER = 'event_scheduler') as res where USER='event_scheduler';";
    private static final String START_DAILY_CLEAN_TASK = "CREATE EVENT IF NOT EXISTS clean_daily ON SCHEDULE EVERY 1 DAY STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY) DO UPDATE " + DailyQuestsTableCreator.TABLE + " SET win_first=0,games_played=0;";
    private static final String START_WEEKLY_CLEAN_TASK = "CREATE EVENT IF NOT EXISTS clean_weekly ON SCHEDULE EVERY 7 DAY STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 7 DAY) DO UPDATE " + WeeklyQuestsTableCreator.TABLE + " SET beds_broken=0;";
    private static final String ENABLE_SCHEDULER = "SET GLOBAL event_scheduler = ON;";

    private void inform() {
        LoggerUtils.logColor(plugin.getLogger(), "&eStarting MySQL data cleaner task.");
    }

    private static void dispatchTasks() {
        MySQLDatabase.getConnection().ifPresent(c -> {

            try (Connection connection = c;
                 PreparedStatement statement = connection.prepareStatement(GET_EVENTSCHEDULER_STATUS);
                 ResultSet res = statement.executeQuery();) {
                if (!res.next()) {
                    try (PreparedStatement enableScheduler = connection.prepareStatement(ENABLE_SCHEDULER)) {
                        enableScheduler.executeUpdate();
                    }
                }
                try (PreparedStatement cleanDaily = connection.prepareStatement(START_DAILY_CLEAN_TASK);
                     PreparedStatement cleanWeekly = connection.prepareStatement(START_WEEKLY_CLEAN_TASK)) {
                    cleanDaily.executeUpdate();
                    cleanWeekly.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public final void startClearTasks() {
        inform();
        dispatchTasks();
    }
}
