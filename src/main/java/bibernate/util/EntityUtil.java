package bibernate.util;

import bibernate.annotation.Column;
import bibernate.annotation.Id;
import bibernate.annotation.Table;
import bibernate.exception.IdFieldNotPresentException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class EntityUtil {

    public static String getColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElse(field.getName());
    }

    public static String getTableName(Class<?> entityType) {
        return Optional.ofNullable(entityType.getAnnotation(Table.class))
                .map(Table::value)
                .orElse(entityType.getSimpleName().toLowerCase(Locale.ROOT));
    }

    public static Field getIdField(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(() -> new IdFieldNotPresentException(
                        String.format("Id not present for '%s'. Please, provide @Id field for this entity",
                                entityType.getSimpleName())));
    }
}
