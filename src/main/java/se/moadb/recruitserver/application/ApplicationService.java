package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Application;
import se.moadb.recruitserver.domain.Status;
import se.moadb.recruitserver.repository.ApplicationRepository;
import se.moadb.recruitserver.repository.StatusRepository;

import java.util.NoSuchElementException;

/**
 * Service that handles logic concerning the /applications API.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@Service
public class ApplicationService  {
   @Autowired
   private ApplicationRepository applicationRepository;
   @Autowired
   private StatusRepository statusRepository;

   /**
    * Accept an application.
    * @param id, referring to the application
    * @return The accepted application.
    */
   public Application accept(long id) {

      Status accepted = statusRepository.findByName("ACCEPTED");

      try {
         Application application = applicationRepository.findById(id).get();
         application.setStatus(accepted);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", id);
      }
   }
   /**
    * Reject an application.
    * @param id, referring to the application
    * @return The rejected application.
    */
   public Application reject(long id) {

      Status rejected = statusRepository.findByName("REJECTED");

      try {
         Application application = applicationRepository.findById(id).get();
         application.setStatus(rejected);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", id);
      }
   }
   /**
    * Unhandle an application.
    * @param id, referring to the application
    * @return The unhandled application.
    */
   public Application unhandle(long id) {
      Status unhandled = statusRepository.findByName("UNHANDLED");

      try {
         Application application = applicationRepository.findById(id).get();
         application.setStatus(unhandled);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", id);
      }

   }
}
