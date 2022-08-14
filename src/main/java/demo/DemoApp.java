package demo;

import bibernate.session.Session;
import bibernate.session.SessionFactory;
import bibernate.session.impl.SessionFactoryImpl;
import demo.entity.Note;
import demo.entity.Person;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class DemoApp {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new SessionFactoryImpl(initializeDataSource());
        Session session = sessionFactory.openSession();

        Person person = session.find(Person.class, 5L);
        System.out.println(person);

        Note note = session.find(Note.class, 7L);
        System.out.println(note);

    }

    private static DataSource initializeDataSource(){
        var dataSource = new PGSimpleDataSource();

        dataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("sa");

        return dataSource;
    }
}
