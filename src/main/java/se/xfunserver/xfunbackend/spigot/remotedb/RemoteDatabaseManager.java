package se.xfunserver.xfunbackend.spigot.remotedb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import se.xfunserver.xfunbackend.services.SQLService;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.files.yml.YMLFile;
import se.xfunserver.xfunbackend.spigot.remotedb.command.CommandModify;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
public class RemoteDatabaseManager extends Module {

    private String HOST;
    private String DATABASE_NAME;
    private String USERNAME;
    private String PASSWORD;
    private String TABLE;
    private Integer PORT;

    private HikariDataSource dataSource1, dataSource2;


    public RemoteDatabaseManager(Core plugin) {
        super(plugin, "Remote Database Manager", false);

        try {
            YMLFile f = this.getPlugin().getPluginManager().getFileStructure().getYMLFile("remote_database");

            this.HOST = f.get().getString("sql.host");
            this.DATABASE_NAME = f.get().getString("sql.database");
            this.PORT = f.get().getInt("sql.port");
            this.USERNAME = f.get().getString("sql.username");
            this.PASSWORD = f.get().getString("sql.password");
            this.TABLE = f.get().getString("sql.table");
        } catch (FileNotFoundException e) {
            Sam.getRobot().error(this, e.getCause().toString(), e.getMessage(), e);
        }

        registerCommand(
                new CommandModify(getPlugin())
        );
    }

    @Override
    public void reload(String response) {

    }

    public long getCurrentCoins(UUID player) {
        String sql =
                String.format(
                        "SELECT `%s` FROM `%s` WHERE `%s`=?",
                        "balance", "xconomy", "UID");

        try (Connection connection = connection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) return result.getLong(1);
                }
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void setCurrentCoins(UUID player, long amount) {
        String sql =
                String.format(
                        "UPDATE `%s` SET `%s`=? WHERE `%s`=?",
                        "xconomy", "balance", "UID");

        try (Connection connection = connection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, amount);
                statement.setString(2, player.toString());
                statement.execute();
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Connection connection() throws SQLException {
        return getDataSource().getConnection();
    }

    public HikariDataSource getDataSource() {
        if (dataSource1 == null) {
            generateNewDataSource(true);
        }
        if (dataSource2 == null) {
            generateNewDataSource(false);
        }

        if (dataSource1.getHikariPoolMXBean().getIdleConnections() <= 0){
            return dataSource2;
        }else{
            return dataSource1;
        }
    }

    private void generateNewDataSource(boolean sourceOne) {
        HikariConfig hikariConfig = getConfig();
        if (sourceOne) {
            dataSource1 = new HikariDataSource(hikariConfig);
        }else{
            dataSource2 = new HikariDataSource(hikariConfig);
        }
    }

    private HikariConfig getConfig(){
        String jdbcUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME + "?useSSL=true&allowPublicKeyRetrieval=true";
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(USERNAME);
        hikariConfig.setPassword(PASSWORD);
        hikariConfig.setMaximumPoolSize(25);
//        hikariConfig.setMinimumIdle(0);
//        hikariConfig.setIdleTimeout(30000);
//        hikariConfig.setConnectionTimeout(60000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true" );
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250" );
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048" );

        return hikariConfig;
    }
}
