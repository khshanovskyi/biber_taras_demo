package bibernate.session.impl;

import bibernate.collection.LazyList;
import bibernate.exception.MoreThanOneResultException;
import bibernate.exception.SessionIsClosedException;
import bibernate.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static bibernate.util.EntityUtil.*;

public class JdbcEntityDao {
    private static final String SELECT_FROM_S_WHERE_COLUMN = "SELECT * FROM %s WHERE %s = ?";

    private DataSource dataSource;
    private boolean open;

    public JdbcEntityDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.open = true;
    }

    @SneakyThrows
    <T> List<T> findAllBy(Class<T> entityType, Field field, Object value) {
        verifyIsSessionOpen();

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
        verifyIsSessionOpen();

        List<T> allBy = findAllBy(entityType, field, value);

        if (allBy.size() > 1) {
            throw new MoreThanOneResultException("Result of the execution query contains more than one entity");
        }

        return allBy.get(0);
    }

    @SneakyThrows
    <T> T findById(Class<T> entityType, Object id) {
        verifyIsSessionOpen();

        var idField = getIdField(entityType);

        return findOneBy(entityType, idField, id);
    }

    void close() {
        this.open = false;
    }

    private void verifyIsSessionOpen() {
        if (!open) {
            throw new SessionIsClosedException("Session has been already closed!");
        }
    }

    @SneakyThrows
    private <T> T getExtractEntityFromResultSet(Class<T> entityType, ResultSet resultSet) {
        T instance = entityType.getConstructor().newInstance();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (isRegularField(field)) {
                setToField(field, instance, resultSet.getObject(getColumnName(field)));
            } else if (isEntityField(field)) {
                var fieldType = field.getType();
                var joinIdField = getIdField(fieldType);
                var columnName = EntityUtil.getColumnName(field);
                var columnValue = resultSet.getObject(columnName);

                var relatedEntity = findOneBy(fieldType, joinIdField, columnValue);

                setToField(field, instance, relatedEntity);
            } else if (isCollectionField(field)) {
                var fieldType = (ParameterizedType) field.getGenericType();
                var relatedEntityType = (Class<?>) fieldType.getActualTypeArguments()[0];

                var entityFieldInRelatedEntity = getRelatedEntityField(entityType, relatedEntityType);

                var entityId = getEntityId(instance);


                var relatedEntityCollection = new LazyList(() -> findAllBy(relatedEntityType, entityFieldInRelatedEntity, entityId));

                setToField(field, instance, relatedEntityCollection);
            }
        }

        return instance;
    }

    @SneakyThrows
    private void setToField(Field field, Object instance, Object value) {
        field.setAccessible(true);
        field.set(instance, value);
    }
}
