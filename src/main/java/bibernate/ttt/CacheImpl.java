package bibernate.ttt;

import bibernate.service.Cache;
import bibernate.util.EntityKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link CacheImpl} provides {@link Cache} and control under it. //todo: think how to write it
 */
public class CacheImpl implements bibernate.ttt.Cache {

    private final Map<EntityKey<?>, Object> cacheMap;
    private boolean enabled;

    /**
     * Creates {@link CacheImpl} with enabled access to {@link CacheImpl#cacheMap}.
     */
    public CacheImpl() {
        cacheMap = new HashMap<>();
        this.enabled = true;
    }

    /**
     * Creates {@link CacheImpl} with enabled or disabled access to {@link CacheImpl#cacheMap}.
     * {@link  CacheImpl#enabled} adjust it.
     *
     * @param enabled flag for adjusting that cache is enabled({@link Boolean#TRUE}) or disabled({@link Boolean#TRUE})
     */
    public CacheImpl(boolean enabled) {
        cacheMap = new HashMap<>();
        this.enabled = enabled;
    }

    /**
     * If cache is enabled then puts <b><entity/b> to the {@link CacheImpl#cacheMap}, where <b><entityKey/b>
     * presented as a key and <b><entity/b> as a value.
     *
     * @param entityKey {@link EntityKey}
     * @param entity    POJO
     * @param <T>       type of entity
     */
    @Override
    public <T> void put(EntityKey<T> entityKey, T entity) {
        if (enabled) {
            cacheMap.put(entityKey, entity);
        }
    }

    /**
     * If cache is enabled then check if {@link CacheImpl#cacheMap} contains entity by its <b><id/b> and
     * <b><entityType/b>. If cache is disabled then provides {@link Optional#empty()}.
     *
     * @param entityType {@link Class} with entity type
     * @param id         id of entity
     * @param <T>        entity type
     * @return {@link Optional} with an entity or {@link Optional#empty()}
     */
    @Override
    public <T> Optional<T> get(Object id, Class<T> entityType) {
        return enabled ? getFromCache(id, entityType) : Optional.empty();
    }

    /**
     * If cache is enabled then deletes a cache from the {@link CacheImpl#cacheMap} by provided <b><entityKey/b>.
     *
     * @param entityKey {@link EntityKey}
     * @param <T>       type of entity
     */
    @Override
    public <T> void delete(EntityKey<T> entityKey) {
        if (enabled) {
            cacheMap.remove(entityKey);
        }
    }

    /**
     * Makes clear for {@link CacheImpl#cacheMap}.
     */
    @Override
    public void clear() {
        cacheMap.clear();
    }

    /**
     * Provides a status about is cache enabled or disabled.
     * If {@link Boolean#TRUE} then the cache is enabled, and it means that you can put, get, delete from
     * {@link CacheImpl#cacheMap}. If {@link Boolean#FALSE} then the cache is disabled, and it means that all
     * operations are accessible but will do nothing.
     *
     * @return {@link Boolean} that represents a status of cache
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Switch cache to enabled state, after this you can make a can put, get, delete from {@link CacheImpl#cacheMap}
     */
    @Override
    public void enable() {
        enabled = true;
    }

    /**
     * Switch cache to enabled state, after this you can make a can put, get, delete from {@link CacheImpl#cacheMap}
     */
    @Override
    public void disable() {
        enabled = false;
    }

    private <T> Optional<T> getFromCache(Object id, Class<T> entityType) {
        return cacheMap.entrySet()
                .stream()
                .filter(entry -> {
                    var entityKey = entry.getKey();
                    return entityKey.entityType().isAssignableFrom(entityType) && entityKey.id().equals(id);
                })
                .map(Map.Entry::getValue)
                .map(entityType::cast)
                .findAny();
    }

}
