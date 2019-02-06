package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.moadb.recruitserver.domain.Status;
import se.moadb.recruitserver.repository.StatusRepository;

import java.util.List;

/**
 * Service that handles logic concerning the /statuses API.
 */
@Service
public class StatusService {

    @Autowired
    StatusRepository statusRepository;

    /**
     * Get all statuses from database.
     * @return list of all statuses.
     */
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }
}
