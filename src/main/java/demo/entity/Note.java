package demo.entity;

import bibernate.annotation.Column;
import bibernate.annotation.Id;
import bibernate.annotation.ManyToOne;
import bibernate.annotation.Table;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table("notes")
public class Note {

    @Id
    private Long id;

    private String body;

    @Column("person_id")
    @ManyToOne
    private Person person;
}
