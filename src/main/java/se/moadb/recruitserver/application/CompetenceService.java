package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Competence;
import se.moadb.recruitserver.repository.CompetenceRepository;

import java.util.ArrayList;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@Service
public class CompetenceService {

    @Autowired
    CompetenceRepository competenceRepository;

    /**
     * @return all competences as a list
     */
    public List<Competence> listAllCompetences(){
        List<Competence> competences = new ArrayList<>();
        competenceRepository.findAll().forEach(competences::add);
        return competences;
    }

}
