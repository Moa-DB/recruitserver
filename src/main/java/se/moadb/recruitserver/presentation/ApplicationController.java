package se.moadb.recruitserver.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
   //@Autowired
   //private ApplicationService applicationService;

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
   public void makeApplication() {}

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
   public String getApplications() {return "hej";}

   /**
    * Change status of an application to "accepted"
    */
   @PutMapping("/{id}/accept")
   public void acceptApplication() {}

   /**
    * Change status of an application to "rejected"
    */
   @PutMapping("/{id}/reject")
   public void rejectApplication() {}

   /**
    * Change status of an application to "unhandled"
    */
   @PutMapping("/{id}/unhandle")
   public void unhandleApplication() {}

}