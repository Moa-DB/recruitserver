package se.moadb.recruitserver.presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a serialized JSON POST request to /applications
 */
public class ApplicationPostRequest {

   private List<CompetenceInPostRequest> competences;
   private List<AvailabilityInPostRequest> available;

   public ApplicationPostRequest() {
      competences = new ArrayList<>();
      available = new ArrayList<>();
   }
   public ApplicationPostRequest(List<CompetenceInPostRequest> competences, List<AvailabilityInPostRequest> available) {
      this.competences = competences;
      this.available = available;
   }

   public List<CompetenceInPostRequest> getCompetences() {
      return competences;
   }

   public void setCompetences(List<CompetenceInPostRequest> competences) {
      this.competences = competences;
   }

   public List<AvailabilityInPostRequest> getAvailable() {
      return available;
   }

   public void setAvailable(List<AvailabilityInPostRequest> available) {
      this.available = available;
   }
}
