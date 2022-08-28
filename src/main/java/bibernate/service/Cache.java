package bibernate.service;

import bibernate.util.EntityKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cache {
    private final Map<EntityKey<?>, Object> cache;

    boolean enabled;

    public Cache() {
        this.cache = new HashMap<>();
        this.enabled = true;
    }

    public Cache(boolean enabled) {
        this.cache = new HashMap<>();
        this.enabled = enabled;
    }

    public Optional<Object> getFromCache(Class<?> entityType, Object id) {
        return enabled ? getCache(entityType, id) : Optional.empty();
    }

    public void add(EntityKey<?> entityKey, Object value) {
        if (enabled) {
            cache.put(entityKey, value);
        }
    }

    public void clear(){
        cache.clear();
    }

    public void enableCashing() {
        enabled = true;
    }

    public void disableCashing() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private Optional<Object> getCache(Class<?> entityType, Object id) {
        return cache.entrySet()
                .stream()
                .filter(entry -> {
                    var entityKey = entry.getKey();
                    return entityKey.entityType().isAssignableFrom(entityType) && entityKey.id() == id;
                })
                .map(Map.Entry::getValue)
                .findAny();
    }

}
