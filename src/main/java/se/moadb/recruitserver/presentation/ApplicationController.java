package se.moadb.recruitserver.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.moadb.recruitserver.application.ApplicationService;
import se.moadb.recruitserver.domain.Application;

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
   public Application makeApplication(@RequestBody ApplicationPostRequest applicationPostRequest) {
      return applicationService.saveApplication(applicationPostRequest);
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
   @GetMapping
   public List<Application> getApplications(Map<String, Object> request) {
      return applicationService.getApplications(request);
   }

   /**
    * Change status of an application to "accepted"
    */
   @PutMapping("/{id}/accept")
   public Application acceptApplication(@PathVariable long id) {
      return applicationService.accept(id);
   }

   /**
    * Change status of an application to "rejected"
    */
   @PutMapping("/{id}/reject")
   public Application rejectApplication(@PathVariable long id) {
      return applicationService.reject(id);
   }

   /**
    * Change status of an application to "unhandled"
    */
   @PutMapping("/{id}/unhandle")
   public Application unhandleApplication(@PathVariable long id) {
      return applicationService.unhandle(id);
   }
}
