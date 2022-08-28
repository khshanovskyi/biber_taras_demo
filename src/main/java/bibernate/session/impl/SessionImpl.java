package bibernate.session.impl;

import bibernate.service.Cache;
import bibernate.session.Session;
import bibernate.util.EntityKey;
import lombok.SneakyThrows;

import javax.sql.DataSource;

public class SessionImpl implements Session {
    private JdbcEntityDao jdbcEntityDao;
    private Cache cache;

    public SessionImpl(DataSource dataSource) {
        jdbcEntityDao = new JdbcEntityDao(dataSource);
        cache = new Cache();
    }

    @Override
    @SneakyThrows
    public <T> T find(Class<T> entityType, Object id) {
        var fromCache = cache.getFromCache(entityType, id);
        return entityType.cast(fromCache.orElseGet(() -> getEntityFromDBById(entityType, id)));
    }

    @Override
    public void close() {
        jdbcEntityDao.close();
        cache.clear();
    }

    private <T> T getEntityFromDBById(Class<T> entityType, Object id) {
        var entity = jdbcEntityDao.findById(entityType, id);

        cache.add(EntityKey.valueOf(entity), entity);
        return entity;
    }

}
