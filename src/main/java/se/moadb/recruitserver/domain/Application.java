package se.moadb.recruitserver.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    //enables optimistic locking. that is, if two users/threads concurrently try to operate on an instance of application, it will use this timestamp to decide if the row was changed while our transcation was going on
    //if such is the case, it will throw OptimisticLockException
    @JsonIgnore
    @Version
    private Timestamp changed;


   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public Timestamp getChanged() {
      return changed;
   }

   public void setChanged(Timestamp changed) {
      this.changed = changed;
   }

   public Application() {
   }

   public Application(Person person, Collection<CompetenceProfile> competenceProfiles, Collection<Availability> availabilities, Status status, Date date) {
      this.person = person;
      this.competenceProfiles = competenceProfiles;
      this.availabilities = availabilities;
      this.status = status;
      this.date = date;
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
