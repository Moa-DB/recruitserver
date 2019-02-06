package se.moadb.recruitserver.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Status {

    //constants, should reflect what is in database
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    public static final String UNHANDLED = "UNHANDLED";

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
