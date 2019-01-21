package se.moadb.recruitserver.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Status {

    @Id
    private String name;

    public Status() {
    }

    public Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
