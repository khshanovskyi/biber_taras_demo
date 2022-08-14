package bibernate.session;

public interface Session {

    <T> T find(Class<T> entityType, Object id);
}
