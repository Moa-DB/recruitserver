package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.moadb.recruitserver.domain.Competence;
import se.moadb.recruitserver.repository.CompetenceRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompetenceService {

    @Autowired
    CompetenceRepository competenceRepository;

    /**
     * Returns all competences as a list
     * @return
     */
    public List<Competence> listAllCompetences(){
        List<Competence> competences = new ArrayList<>();
        competenceRepository.findAll().forEach(competences::add);
        return competences;
    }


}
