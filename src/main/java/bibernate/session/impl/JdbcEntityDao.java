package bibernate.session.impl;

import bibernate.exception.MoreThanOneResultException;
import bibernate.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static bibernate.util.EntityUtil.*;

@RequiredArgsConstructor
public class JdbcEntityDao {
    private static final String SELECT_FROM_S_WHERE_COLUMN = "SELECT * FROM %s WHERE %s = ?";

    private final DataSource dataSource;

    @SneakyThrows
    <T> List<T> findAllBy(Class<T> entityType, Field field, Object value) {
        List<T> result = new ArrayList<>();

        try (var connection = dataSource.getConnection()) {
            var sqlQuery = String.format(SELECT_FROM_S_WHERE_COLUMN, getTableName(entityType), getColumnName(field));
            try (var statement = connection.prepareStatement(sqlQuery)) {
                statement.setObject(1, value);

                System.out.println("SQL: " + sqlQuery);

                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(getExtractEntityFromResultSet(entityType, resultSet));
                }
            }
        }

        return result;
    }

    @SneakyThrows
    <T> T findOneBy(Class<T> entityType, Field field, Object value) {
        List<T> allBy = findAllBy(entityType, field, value);

        if (allBy.size() > 1){
            throw new MoreThanOneResultException("Result of the execution query contains more than one entity");
        }

        return allBy.get(0);
    }

    @SneakyThrows
    <T> T findById(Class<T> entityType, Object id) {
        var idField = getIdField(entityType);

        return findOneBy(entityType, idField, id);
    }

    @SneakyThrows
    private <T> T getExtractEntityFromResultSet(Class<T> entityType, ResultSet resultSet) {
        T instance = entityType.getConstructor().newInstance();

        Arrays.stream(instance.getClass().getDeclaredFields())
                .forEach(field -> {
                    try {
                        setToField(field, instance, resultSet.getObject(getColumnName(field)));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

        return instance;
    }

    @SneakyThrows
    private void setToField(Field field, Object instance, Object value) {
        field.setAccessible(true);
        field.set(instance, value);
    }
}
