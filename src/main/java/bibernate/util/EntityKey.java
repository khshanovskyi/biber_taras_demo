package bibernate.util;

public record EntityKey<T>(Class<T> entityType, Object id) {

    public static <T> EntityKey<T> of(Class<T> entityType, Object id) {
        return new EntityKey<>(entityType, id);
    }

    public static <T> EntityKey<?> valueOf(T entity) {
        var id = EntityUtil.getEntityId(entity);
        var entityType = entity.getClass();
        return EntityKey.of(entityType, id);
    }
}
