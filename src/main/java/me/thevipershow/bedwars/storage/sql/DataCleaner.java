package me.thevipershow.bedwars.storage.sql;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.storage.sql.tables.DataResetTableUtils;
import me.thevipershow.bedwars.storage.sql.tables.QuestsTableUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class DataCleaner {

    private final Plugin plugin;

    public DataCleaner(final Plugin plugin) {
        this.plugin = plugin;
    }

    private final BukkitTask[] cleanTask = new BukkitTask[2];

    public final void startTasks() {
        if (cleanTask[0] == null && cleanTask[1] == null) {
            final Calendar nextWeekCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
            final Calendar originalCalendar = (Calendar) nextWeekCalendar.clone();
            int dayOfMonth = nextWeekCalendar.get(Calendar.DAY_OF_MONTH);
            nextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
            nextWeekCalendar.set(Calendar.MINUTE, 1);

            while (dayOfMonth % 7 != 0) {
                nextWeekCalendar.add(Calendar.DAY_OF_MONTH, 1);
                dayOfMonth = nextWeekCalendar.get(Calendar.DAY_OF_MONTH);
            }

            final long untilWeek = originalCalendar.toInstant().until(nextWeekCalendar.toInstant(), ChronoUnit.SECONDS);

            this.cleanTask[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                DataResetTableUtils.getWeeklyTime(plugin).thenAccept(value -> {
                    if (value == null || value == 0 || (System.currentTimeMillis() - value >= (1000 * 60 * 60 * 24 * 7))) {
                        QuestsTableUtils.clearWeeklyData(plugin);
                        DataResetTableUtils.updateWeeklyTime(plugin);
                    }
                });
            }, untilWeek * 20L, 20L * 60L * 60L * 24L * 7L);

            final Calendar nextDayCalendar = (Calendar) originalCalendar.clone();
            nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
            nextDayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            nextDayCalendar.set(Calendar.MINUTE, 1);
            final long untilDay = originalCalendar.toInstant().until(nextDayCalendar.toInstant(), ChronoUnit.SECONDS);

            this.cleanTask[1] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                DataResetTableUtils.getDailyTime(plugin).thenAccept(value -> {
                    if (value == null || value == 0 || (System.currentTimeMillis() - value >= (1000 * 60 * 60 * 24))) {
                        QuestsTableUtils.clearDailyData(plugin);
                        DataResetTableUtils.updateDailyTime(plugin);
                    }
                });
            }, untilDay * 20L, 20L * 60L * 60L * 24L);

        }
    }

    public final void stopTasks() {
        for (final BukkitTask task : this.cleanTask) {
            if (task != null) {
                LoggerUtils.logColor(plugin.getLogger(), AllStrings.REMOVE_CLEAN_TASK.get() + task.getTaskId());
                task.cancel();
            }
        }
    }
}
