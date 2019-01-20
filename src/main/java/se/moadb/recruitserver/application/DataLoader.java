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
    UserDetailsService userDetailsService;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    CompetenceProfileRepository competenceProfileRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    //TODO: When do we migrate? This condition is not good.
        if(personRepository.findByOldId(1L) == null){
            runSqlFromFile("oldDB.sql");
            runSqlFromFile("oldData.sql");

            migrateOldToNew();
            deleteOldTables();
        }

    }

    private void migrateOldToNew(){

        jdbcTemplate.query(
                "SELECT * FROM OLDcompetence", new Object[] {},
                (rs, rowNum) -> competenceRepository.save(new Competence(rs.getString("name"), rs.getLong("competence_id"))));

        jdbcTemplate.query(
                "SELECT * FROM OLDrole", new Object[] {},
                (rs, rowNum) -> roleRepository.save(new Role(rs.getString("name"), rs.getLong("role_id"))));

        //If no username can be found, a new user is created with the value of the surname row  as password.
        // In real world, a safer password could be generated and sent to the user trough email.
        jdbcTemplate.query(
                "SELECT * FROM OLDperson", new Object[] {},
                (rs, rowNum) ->
                {
                    if(rs.getString("username") != null) {
                        return personRepository.save(new Person(rs.getString("name"), rs.getString("surname"),
                                rs.getString("ssn"), rs.getString("email"), userDetailsService.saveUser(rs.getString("username"),
                                rs.getString("password"), roleRepository.findById(rs.getLong("role_id")).getName()), rs.getLong("person_id")));
                    }
                    else {
                        return  personRepository.save(new Person(rs.getString("name"), rs.getString("surname"),
                                rs.getString("ssn"), rs.getString("email"), userDetailsService.saveUser(rs.getString("name"),
                                rs.getString("surname"), roleRepository.findById(rs.getLong("role_id")).getName()), rs.getLong("person_id")));
                    }
                }
        );

        jdbcTemplate.query(
                "SELECT * FROM OLDavailability", new Object[] {},
                (rs, rowNum) -> availabilityRepository.save(new Availability( personRepository.findByOldId(rs.getLong("person_id")), rs.getDate("from_date"), rs.getDate("to_date"))));

        jdbcTemplate.query(
                "SELECT * FROM OLDcompetence_profile", new Object[] {},
                (rs, rowNum) -> competenceProfileRepository.save(new CompetenceProfile( personRepository.findByOldId(rs.getLong("person_id")), competenceRepository.findByOldId(rs.getLong("competence_id")), rs.getDouble("years_of_experience"))));


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
