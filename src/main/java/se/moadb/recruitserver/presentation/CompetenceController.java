package se.moadb.recruitserver.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.moadb.recruitserver.application.CompetenceService;
import se.moadb.recruitserver.domain.Competence;

import java.util.List;

/**
 * Entry point for REST requests concerning competences.
 * Implemented methods:
 * - GET /competences, get all competences as a list
 */
@RestController
@RequestMapping("/competences")
public class CompetenceController {

    @Autowired
    CompetenceService competenceService;

    @GetMapping
    public List<Competence> listAllCompetences(){
        return competenceService.listAllCompetences();
    }

}
