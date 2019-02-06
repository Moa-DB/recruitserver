package se.moadb.recruitserver.presentation;

/**
 * Represents a JSON PUT request to change a status, to /accept, /reject, or /unhandled
 */
public class ApplicationStatusPutRequest {
   private String status;

   public ApplicationStatusPutRequest() {

   }
   public ApplicationStatusPutRequest(String status) {
      this.status = status;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}