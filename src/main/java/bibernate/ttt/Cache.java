package bibernate.ttt;

import bibernate.util.EntityKey;

import java.util.Optional;

public interface Cache {

    <T> void put(EntityKey<T> entityKey, T entity);

    <T> Optional<T> get(Object id, Class<T> entityType);

    <T> void delete(EntityKey<T> entityKey);

    void clear();

    boolean isEnabled();

    void enable();

    void disable();
}
