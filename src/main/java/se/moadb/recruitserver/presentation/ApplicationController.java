package se.moadb.recruitserver.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.moadb.recruitserver.application.ApplicationService;
import se.moadb.recruitserver.domain.Application;


import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Entry point for REST requests concerning applications.
 * Implemented methods:
 * - POST /applications, send in a new application.
 * - GET /applications, get all applications, or applications conforming to search criteria
 * - PUT /applications/:id/accept, accept an application
 * - PUT /applications/:id/reject, reject an application
 * - PUT /applications/:id/unhandle, unhandle an application
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController {
   @Autowired
   private ApplicationService applicationService;

   /**
    * Submit a job application. Applications are stored as "unhandled".
    * JSON data should be structured as follows:
    * {
    *    person_id : int,
    *    competences : [
    *       {
    *          competence : string,
    *          years_of_experience : int
    *       }
    *    ],
    *    available : [
    *       {
    *          from: date,
    *          to: date
    *       }
    *    ]
    * }
    */
   @PostMapping
   public Application makeApplication(@RequestBody ApplicationPostRequest applicationPostRequest, Principal principal) {
      return applicationService.saveApplication(applicationPostRequest, principal.getName());
   }

   /**
    * Get applications.
    * No attached JSON data means you get all applications.
    * JSON can be used to filter data and should be structured as follows:
    * {
    *    name : string,
    *    application_date : date,
    *    competence : string,
    *    from_time : date,
    *    to_time : date
    * }
    * All keys are optional, that is, you can specify none, one, or any number of them together.
    */
   @PostMapping("/filter")
   public List<Application> getApplications(@RequestBody Map<String, Object> request) {
      return applicationService.getApplications(request);
   }

   /**
    * Change status of an application to "accepted"
    * @throws se.moadb.recruitserver.application.ConcurrentAccessException: HTTP 409: Conflict, if accessing an application that was simultaneously altered by another user.
    */
   @PutMapping("/{id}/accept")
   public Application acceptApplication(@PathVariable long id, @RequestBody ApplicationStatusPutRequest applicationStatusPutRequest) {
      String oldStatusToCompareWith = applicationStatusPutRequest.getStatus();
      return applicationService.accept(id, oldStatusToCompareWith);
   }

   /**
    * Change status of an application to "rejected"
    * @throws se.moadb.recruitserver.application.ConcurrentAccessException: HTTP 409: Conflict, if accessing an application that was simultaneously altered by another user.
    */
   @PutMapping("/{id}/reject")
   public Application rejectApplication(@PathVariable long id, @RequestBody ApplicationStatusPutRequest applicationStatusPutRequest) {
      String oldStatusToCompareWith = applicationStatusPutRequest.getStatus();
      return applicationService.reject(id, oldStatusToCompareWith);
   }

   /**
    * Change status of an application to "unhandled"
    * @throws se.moadb.recruitserver.application.ConcurrentAccessException: HTTP 409: Conflict, if accessing an application that was simultaneously altered by another user.
    */
   @PutMapping("/{id}/unhandle")
   public Application unhandleApplication(@PathVariable long id, @RequestBody ApplicationStatusPutRequest applicationStatusPutRequest) {
      String oldStatusToCompareWith = applicationStatusPutRequest.getStatus();
      return applicationService.unhandle(id, oldStatusToCompareWith);
   }
}
