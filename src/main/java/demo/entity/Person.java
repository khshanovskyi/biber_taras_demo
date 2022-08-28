package demo.entity;

import bibernate.annotation.Column;
import bibernate.annotation.Id;
import bibernate.annotation.OneToMany;
import bibernate.annotation.Table;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Table("persons")
public class Person {

    @Id
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @OneToMany(mappedBy = "person")
    private List<Note> notes = new ArrayList<>();
}
