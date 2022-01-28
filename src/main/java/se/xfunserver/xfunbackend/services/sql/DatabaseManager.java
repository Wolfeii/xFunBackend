package se.xfunserver.xfunbackend.services.sql;

import lombok.Getter;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.services.SQLService;
import se.xfunserver.xfunbackend.spigot.user.object.UserExperience;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class DatabaseManager {

    /**
     * TODO: Disconnect connection after a while with no queries
     *
     * https://www.baeldung.com/java-connection-pooling
     * https://stackoverflow.com/questions/34117164/java-async-mysql-queries
     *
     */

    private final SQLService sqlService;

    public DatabaseManager(SQLService service){
        this.sqlService = service;
    }

    public void loadDefaultTables(){
        // Generate Tables
        new TableBuilder("users", this)
                .addColumn("uuid", SQLDataType.BINARY, 16,false, SQLDefaultType.NO_DEFAULT, true)
                .addColumn("name", SQLDataType.VARCHAR, 100,false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("rank", SQLDataType.VARCHAR, 100,false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("user_experience", SQLDataType.VARCHAR, 100,false, SQLDefaultType.CUSTOM.setCustom(UserExperience.NOOB.toString().toUpperCase()), false)
                .addColumn("joined_on", SQLDataType.TIMESTAMP, -1, true, SQLDefaultType.NO_DEFAULT, false)
                .execute();
    }

    public boolean update(String table, Map<String, Object> data, Map<String, Object> whereData){
        HashMap<Integer, Object> indexed = new HashMap<>();
        try {
            StringBuilder query = new StringBuilder("UPDATE xfun_"+table+" SET ");

            data.remove("uuid");
            data.remove("id");

            final int[] a = {1};
            data.forEach((s, o) -> {
                if (a[0] >1) query.append(", ");
                query.append("`").append(s).append("`").append("=?");
                indexed.put(a[0], o);
                a[0]++;
            });

            query.append(" WHERE ");

            AtomicInteger i = new AtomicInteger();
            whereData.forEach((s, o) -> {
                if (i.get() > 0) query.append(" AND ");
                query.append("`").append(s).append("`").append("=?");
                indexed.put(a[0], o);
                a[0]++;
                i.getAndIncrement();
            });

            try(Connection connection = SQLService.connection()){

                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

                for(Integer index : indexed.keySet()){
                    Object value = indexed.get(index);

                    if (value instanceof InputStream){
                        preparedStatement.setBinaryStream(index, (InputStream) value);
                        continue;
                    }
                    preparedStatement.setObject(index, value);
                }

                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();
            }
            return true;
        }catch (SQLException e){
            xFunLogger.warn("Can't execute update statement. " + e.getMessage());
//            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getResults(String table, String where, Map<Integer, Object> data) throws SQLException {
        return getResults("xfun", table, where, data);
    }

    public ResultSet getResults(String tablePrefix, String table, String where, Map<Integer, Object> data) throws SQLException {
        StringBuilder query = new StringBuilder(
                "SELECT * FROM "+(tablePrefix==null?"":tablePrefix+"_")+table+(where != null ? (" WHERE "+where) : "")
        );

        try(Connection connection = SQLService.connection()) {
            PreparedStatement statement = connection.prepareStatement(
                    query.toString()
            );

            if (where != null) {
                for (int b : data.keySet()) {
                    Object object = data.get(b);

                    if (object instanceof InputStream) {
                        statement.setBinaryStream(b, (InputStream) object);
                        continue;
                    }
                    statement.setObject(b, object);
                }
            }

            ResultSet resultSet = statement.executeQuery();
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet crs = factory.createCachedRowSet();
            crs.populate(resultSet);

            statement.close();
            resultSet.close();
            connection.close();

            return crs;
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            try(Connection connection = SQLService.connection()) {
                ResultSet resultSet = connection.prepareStatement(query).executeQuery();

                RowSetFactory factory = RowSetProvider.newFactory();
                CachedRowSet crs = factory.createCachedRowSet();
                crs.populate(resultSet);

                resultSet.close();
                connection.close();

                return crs;
            }
        }catch (SQLException e){
            xFunLogger.warn("Can't executeQuery statement. " + e.getMessage());
        }
        return null;
    }

    public void execute(String query){
        try {
            try(Connection connection = SQLService.connection()) {
                connection.prepareStatement(query).execute();

                connection.close();
            }
        }catch (SQLException e){
            xFunLogger.warn("Can't execute statement. " + e.getMessage());
        }
    }

    public void executeUpdate(String query){
        try {
            try(Connection connection = SQLService.connection()) {
                connection.prepareStatement(query).executeUpdate();

                connection.close();
            }
        }catch (SQLException e){
            xFunLogger.warn("Can't executeUpdate statement. " + e.getMessage());
        }
    }

    public boolean insert(String table, Map<String, Object> data){
        return execute("INSERT INTO", table, data, true);
    }

    public boolean remove(String table, Map<String, Object> whereData) {
        HashMap<Integer, Object> indexed = new HashMap<>();
        try {
            StringBuilder query = new StringBuilder("DELETE FROM xfun_"+table+" WHERE ");
            final int[] a = {1};

            AtomicInteger i = new AtomicInteger();
            whereData.forEach((s, o) -> {
                if (i.get() > 0) query.append(" AND ");
                query.append("`").append(s).append("`").append("=?");
                indexed.put(a[0], o);
                a[0]++;
                i.getAndIncrement();
            });

            xFunLogger.debug("Making a remove query as follows: " + query.toString());
            try(Connection connection = SQLService.connection()){

                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

                for(Integer index : indexed.keySet()){
                    Object value = indexed.get(index);

                    if (value instanceof InputStream){
                        preparedStatement.setBinaryStream(index, (InputStream) value);
                        continue;
                    }
                    preparedStatement.setObject(index, value);
                }

                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();
            }
            return true;
        }catch (SQLException e){
//            xFunLogger.warn("Can't execute remove statement. " + e.getMessage());
//            e.printStackTrace();
            return false;
        }
    }

    public boolean execute(String prefix, String table, Map<String, Object> data, boolean insert) {
        HashMap<Integer, Object> indexed = new HashMap<>();
        try {
            StringBuilder query = new StringBuilder(prefix + " xfun_" + table + " ("),
                    values = new StringBuilder(") VALUES(");

            int a=1;
            for(String key : data.keySet()){
                if (a>1) {
                    query.append(", ");
                    values.append(", ");
                }
                query.append("`").append(key).append("`");
                values.append('?');

                indexed.put(a, data.get(key));
                a++;
            }

            values.append(")");
            query.append(values.toString());

            xFunLogger.debug(query.toString());
            xFunLogger.debug(data + " > is the values data being sent.");

            try(Connection connection = SQLService.connection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

                for (Integer index : indexed.keySet()) {
                    Object value = indexed.get(index);

                    if (value instanceof InputStream) {
                        preparedStatement.setBinaryStream(index, (InputStream) value);
                        continue;
                    }
                    preparedStatement.setObject(index, value);
                }

                preparedStatement.executeUpdate();

                preparedStatement.close();

                connection.close();
            }
            return true;
        }catch (SQLException e){
//            xFunLogger.warn("Can't execute statement. " + e.getMessage());
            return false;
        }
    }

}