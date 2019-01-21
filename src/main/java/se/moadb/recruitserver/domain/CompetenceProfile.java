package se.moadb.recruitserver.domain;

import javax.persistence.*;

@Entity
public class CompetenceProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private Person person;
    @OneToOne
    private Competence competence;
    private double yearsOfExperience;

    public CompetenceProfile() {
    }

    public CompetenceProfile(Person person, Competence competence, double yearsOfExperience) {
        this.person = person;
        this.competence = competence;
        this.yearsOfExperience = yearsOfExperience;
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

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public double getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
