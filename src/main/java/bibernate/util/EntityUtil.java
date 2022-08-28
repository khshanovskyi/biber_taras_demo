package bibernate.util;

import bibernate.annotation.*;
import bibernate.exception.IdFieldNotPresentException;
import bibernate.exception.RelatedEntityNotFoundException;

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

    public static Object getEntityId(Object entity) {
        var idField = EntityUtil.getIdField(entity.getClass());
        try {
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRegularField(Field field) {
        return !isEntityField(field) && !isCollectionField(field);
    }

    public static boolean isEntityField(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public static boolean isCollectionField(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    public static <T> Field getRelatedEntityField(Class<T> fromEntity, Class<?> toEntity) {
        return Arrays.stream(toEntity.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(fromEntity))
                .findAny()
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        String.format("Cannot find related entity to '%s' from '%s'", toEntity, fromEntity)));
    }
}
