package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import se.moadb.recruitserver.domain.*;
import se.moadb.recruitserver.repository.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


/**
 * DataLoader loads the database with data when the application is started. The environment variable "MIGRATE" set on the server
 * determines if old data shall be migrated in to the current database.
 */
@Component
public class DataLoader implements ApplicationRunner {

    //Used to translate ids from old datatbase to ids in new database.
    private HashMap<Long, String> role = new HashMap<>();
    private HashMap<Long, Long> person = new HashMap<>();
    private HashMap<Long, Long> availability = new HashMap<>();
    private HashMap<Long, String> competence = new HashMap<>();
    private HashMap<Long, Long> competenceProfile = new HashMap<>();


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CompetenceRepository competenceRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SecurityService userDetailsService;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    CompetenceProfileRepository competenceProfileRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    ApplicationRepository applicationRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        loadData();

        //Reads value from environment variable which can be set in Travis and Heroku if migration is wanted.
        String migrate = System.getenv("MIGRATE");
        //migrate = "TRUE";
        migrate = "FALSE";

        if(migrate.equals("TRUE")){
            runSqlFromFile("oldDB.sql");
            runSqlFromFile("oldData.sql");

            migrateOldToNew();
            createApplications();
            deleteOldTables();
        }

    }

    private void loadData(){
        Status accepted = new Status("ACCEPTED");
        Status rejected = new Status("REJECTED");
        Status unhandled = new Status("UNHANDLED");
        statusRepository.save(accepted);
        statusRepository.save(rejected);
        statusRepository.save(unhandled);
    }

    /**
     * Uses jdbcTemplate to communicate directly with database. Takes data from old tables and modifies it to fit in the new tables.
     * Hashmaps are used to store translations between ids of old tables and ids of new tables.
     */
    private void migrateOldToNew(){

        jdbcTemplate.query(
                "SELECT * FROM OLDcompetence", new Object[] {},
                (rs, rowNum) -> competence.put( rs.getLong("competence_id"), competenceRepository.save(new Competence(rs.getString("name"))).getName()));

        jdbcTemplate.query(
                "SELECT * FROM OLDrole", new Object[] {},
                (rs, rowNum) -> role.put( rs.getLong("role_id"), roleRepository.save(new Role(rs.getString("name"))).getName()));

        //If no username can be found, a new user is created with the value of the surname row  as password.
        // In real world, a safer password could be generated and sent to the user trough email.
        jdbcTemplate.query(
                "SELECT * FROM OLDperson", new Object[] {},
                (rs, rowNum) ->
                {
                    if(rs.getString("username") != null) {
                        return person.put( rs.getLong("person_id"), personRepository.save(new Person(rs.getString("name"), rs.getString("surname"),
                                rs.getString("ssn"), rs.getString("email"), userDetailsService.saveUser(rs.getString("username"),
                                rs.getString("password"), roleRepository.findByName(role.get( rs.getLong("role_id"))).getName()))).getId());
                    }
                    else {
                        return  person.put( rs.getLong("person_id"), personRepository.save(new Person(rs.getString("name"), rs.getString("surname"),
                                rs.getString("ssn"), rs.getString("email"), userDetailsService.saveUser(rs.getString("name"),
                                rs.getString("surname"), roleRepository.findByName(role.get( rs.getLong("role_id"))).getName()))).getId());
                    }
                }
        );

        jdbcTemplate.query(
                "SELECT * FROM OLDavailability", new Object[] {},
                (rs, rowNum) ->  availability.put( availabilityRepository.save(new Availability( rs.getDate("from_date"), rs.getDate("to_date"))).getId(), rs.getLong("person_id")));

        jdbcTemplate.query(
                "SELECT * FROM OLDcompetence_profile", new Object[] {},
                (rs, rowNum) -> competenceProfile.put( competenceProfileRepository.save(new CompetenceProfile( competenceRepository.findByName(competence.get(rs.getLong("competence_id"))), rs.getDouble("years_of_experience"))).getId(), rs.getLong("person_id")));


    }

    /**
     * Creates applications from "CompetenceProfiles" and "Availabilities" retrieved from the old tables.
     * Iterates all persons stored in the "persons" hashmap and checks if the person is an applicant, otherwise no application shall be created.
     * If the person is an appliacant, all CompetenceProfiles stored in the "competenceProfiles" hashmap is iterated trough and entries with a
     * matching "person_id" is used to find the CompetenceProfile in the new database and add it to a set of CompetenceProfiles. The same goes for
     * "Availabilities". The Application can then be created and saved to the database.
     */
    private void createApplications(){
        Application application;
        Person applicant;
        Role role = roleRepository.findByName("applicant");
        Status status = statusRepository.findByName("UNHANDLED");
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        for (Map.Entry<Long, Long> person : person.entrySet()){
            applicant = personRepository.findById(person.getValue());
            if(applicant.getUser().getRoles().contains(role)){
                Set<CompetenceProfile> competenceProfiles = new HashSet<>();
                Set<Availability> availabilities = new HashSet<>();
                for (Map.Entry<Long, Long> competenceProfile : competenceProfile.entrySet()){
                    if(Objects.equals(competenceProfile.getValue(), person.getKey())){
                        competenceProfiles.add(competenceProfileRepository.findById(competenceProfile.getKey()));
                    }
                }
                for (Map.Entry<Long, Long> availabilitys : availability.entrySet()){
                    if(Objects.equals(availabilitys.getValue(), person.getKey())){
                        availabilities.add(availabilityRepository.findById(availabilitys.getKey()));
                    }
                }
                application = applicationRepository.save(new Application(applicant, null, null, status, date));

                application = applicationRepository.findById(application.getId()).get(); //without re-finding this, the we get an optimistic locking error cause version changed within transaction. now new transaction starts and were fine
                application.setAvailabilities(availabilities);
                application.setCompetenceProfiles(competenceProfiles);
                applicationRepository.save(application);
            }
        }

    }

    /**
     * Removes old tables used when migrating old data.
     */
    private void deleteOldTables(){
        jdbcTemplate.execute("DROP TABLE OLDcompetence");
        jdbcTemplate.execute("DROP TABLE OLDrole");
        jdbcTemplate.execute("DROP TABLE OLDperson");
        jdbcTemplate.execute("DROP TABLE OLDavailability");
        jdbcTemplate.execute("DROP TABLE OLDcompetence_profile");
    }

    /**
     * Loads text from file and separates statements by ";". "OLD" is added to all table names to not interfere with new tables
     * with same names. Statements are then executed.
     * @param fileName
     * @throws IOException
     */
    private void runSqlFromFile(String fileName) throws IOException {
        String[] sqlStatements = StreamUtils.copyToString( new ClassPathResource(fileName).getInputStream(), Charset.defaultCharset()).split(";");
        for (String sqlStatement : sqlStatements) {
            sqlStatement = sqlStatement.replace("CREATE TABLE ", "CREATE TABLE OLD");
            sqlStatement = sqlStatement.replace("REFERENCES ", "REFERENCES OLD");
            sqlStatement = sqlStatement.replace("INSERT INTO ", "INSERT INTO OLD");
            jdbcTemplate.execute(sqlStatement);
        }
    }

}
