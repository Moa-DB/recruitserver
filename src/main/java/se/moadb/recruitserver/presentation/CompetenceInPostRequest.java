package se.moadb.recruitserver.presentation;

/**
 * Represents a JSON object containing information on a competence in a POST request.
 */
public class CompetenceInPostRequest {

   private String competence;
   private Integer years_of_experience;

   public CompetenceInPostRequest() {

   }
   public CompetenceInPostRequest(String competence, Integer years_of_experience) {
      this.competence = competence;
      this.years_of_experience = years_of_experience;
   }

   public String getCompetence() {
      return competence;
   }

   public void setCompetence(String competence) {
      this.competence = competence;
   }

   public Integer getYears_of_experience() {
      return years_of_experience;
   }

   public void setYears_of_experience(Integer years_of_experience) {
      this.years_of_experience = years_of_experience;
   }
}
