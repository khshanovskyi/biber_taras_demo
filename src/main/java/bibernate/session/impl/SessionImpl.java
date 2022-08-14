package bibernate.session.impl;

import bibernate.session.Session;
import lombok.SneakyThrows;

import javax.sql.DataSource;

public class SessionImpl implements Session {
    private final JdbcEntityDao jdbcEntityDao;

    public SessionImpl(DataSource dataSource) {
        jdbcEntityDao = new JdbcEntityDao(dataSource);
    }

    @Override
    @SneakyThrows
    public <T> T find(Class<T> entityType, Object id) {
        return jdbcEntityDao.findById(entityType, id);
    }

}
