package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import se.moadb.recruitserver.domain.*;
import se.moadb.recruitserver.repository.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

@Component
public class DataLoader implements ApplicationRunner {

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

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String migrate = System.getenv("MIGRATE");
        //migrate = "TRUE";

        if(migrate.equals("TRUE")){
            runSqlFromFile("oldDB.sql");
            runSqlFromFile("oldData.sql");

            migrateOldToNew();
            deleteOldTables();
        }

    }

    private void migrateOldToNew(){

        HashMap<String, String> translation = new HashMap<>();

        jdbcTemplate.query(
                "SELECT * FROM OLDcompetence", new Object[] {},
                (rs, rowNum) -> translation.put( "competence" + rs.getLong("competence_id"), competenceRepository.save(new Competence(rs.getString("name"))).getName()));

        jdbcTemplate.query(
                "SELECT * FROM OLDrole", new Object[] {},
                (rs, rowNum) -> translation.put( "role" + rs.getLong("role_id"), roleRepository.save(new Role(rs.getString("name"))).getName()));

        //If no username can be found, a new user is created with the value of the surname row  as password.
        // In real world, a safer password could be generated and sent to the user trough email.
        jdbcTemplate.query(
                "SELECT * FROM OLDperson", new Object[] {},
                (rs, rowNum) ->
                {
                    if(rs.getString("username") != null) {
                        return translation.put( "person" + rs.getLong("person_id"), String.valueOf(personRepository.save(new Person(rs.getString("name"), rs.getString("surname"),
                                rs.getString("ssn"), rs.getString("email"), userDetailsService.saveUser(rs.getString("username"),
                                rs.getString("password"), roleRepository.findByName(translation.get("role" + rs.getLong("role_id"))).getName()))).getId()));
                    }
                    else {
                        return  translation.put( "person" + rs.getLong("person_id"), String.valueOf(personRepository.save(new Person(rs.getString("name"), rs.getString("surname"),
                                rs.getString("ssn"), rs.getString("email"), userDetailsService.saveUser(rs.getString("name"),
                                rs.getString("surname"), roleRepository.findByName(translation.get("role" + rs.getLong("role_id"))).getName()))).getId()));
                    }
                }
        );

        jdbcTemplate.query(
                "SELECT * FROM OLDavailability", new Object[] {},
                (rs, rowNum) -> availabilityRepository.save(new Availability( rs.getDate("from_date"), rs.getDate("to_date"))));

        jdbcTemplate.query(
                "SELECT * FROM OLDcompetence_profile", new Object[] {},
                (rs, rowNum) -> competenceProfileRepository.save(new CompetenceProfile( competenceRepository.findByName(translation.get("competence" + rs.getLong("competence_id"))), rs.getDouble("years_of_experience"))));


    }

    private void deleteOldTables(){
        jdbcTemplate.execute("DROP TABLE OLDcompetence");
        jdbcTemplate.execute("DROP TABLE OLDrole");
        jdbcTemplate.execute("DROP TABLE OLDperson");
        jdbcTemplate.execute("DROP TABLE OLDavailability");
        jdbcTemplate.execute("DROP TABLE OLDcompetence_profile");
    }

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
