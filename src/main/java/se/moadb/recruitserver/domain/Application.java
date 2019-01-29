package se.moadb.recruitserver.domain;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Person person;

    @OneToMany(cascade = CascadeType.ALL) //to implicitly save new profiles that are created on POST
    private Collection<CompetenceProfile> competenceProfiles;

    @OneToMany(cascade = CascadeType.ALL) //see competenceprofiles on why cascade
    private Collection<Availability> availabilities;

    @ManyToOne
    private Status status;

    public Application() {
    }

    public Application(Person person, Collection<CompetenceProfile> competenceProfiles, Collection<Availability> availabilities, Status status) {
        this.person = person;
        this.competenceProfiles = competenceProfiles;
        this.availabilities = availabilities;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Collection<CompetenceProfile> getCompetenceProfiles() {
        return competenceProfiles;
    }

    public void setCompetenceProfiles(Collection<CompetenceProfile> competenceProfiles) {
        this.competenceProfiles = competenceProfiles;
    }

    public Collection<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(Collection<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
