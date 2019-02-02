package se.moadb.recruitserver.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.moadb.recruitserver.application.StatusService;
import se.moadb.recruitserver.domain.Status;

import java.util.List;

/**
 * Entry point for REST requests concerning statuses.
 * Implemented methods:
 *  - GET /statuses, get all statuses.
 */
@RestController
@RequestMapping("/statuses")
public class StatusController {

    @Autowired
    StatusService statusService;

    /**
     * GET all statuses.
     * @return list of all statuses.
     */
    @GetMapping
    public List<Status> getAllStatuses(){
        return statusService.getAllStatuses();
    }
}
