package se.moadb.recruitserver.domain;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Competence {

    @Id
    private String name;

    private Long oldId;

    public Competence() {
    }

    public Competence(String name, Long oldId) {
        this.name = name;
        this.oldId = oldId;
    }
}
