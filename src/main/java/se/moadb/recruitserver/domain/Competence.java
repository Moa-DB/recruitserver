package se.moadb.recruitserver.domain;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Competence {

    @Id
    private String name;

    public Competence() {
    }

    public Competence(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
