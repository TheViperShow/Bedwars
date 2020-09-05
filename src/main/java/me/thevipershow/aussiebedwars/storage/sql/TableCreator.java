package me.thevipershow.aussiebedwars.storage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class TableCreator {
    private final String tableName;
    private final String dataTypes;
    private final Connection connection;

    public TableCreator(String tableName, String dataTypes, Connection connection) {
        this.tableName = tableName;
        this.dataTypes = dataTypes;
        this.connection = connection;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDataTypes() {
        return dataTypes;
    }

    public void createTable() {
        try (Connection connection = this.connection;
             PreparedStatement preparedStatement = connection.prepareStatement(
                     String.format("CREATE TABLE IF NOT EXISTS %s (%s);", tableName, dataTypes))) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
