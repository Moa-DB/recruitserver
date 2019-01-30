package se.moadb.recruitserver.presentation;

import java.sql.Date;

/**
 * Represents a JSON object containing availability dates in a POST request.
 */
public class AvailabilityInPostRequest {

   private Date from;
   private Date to;

   public AvailabilityInPostRequest() {

   }
   public AvailabilityInPostRequest(Date from, Date to) {
      this.from = from;
      this.to = to;
   }

   public Date getFrom() {
      return from;
   }

   public void setFrom(Date from) {
      this.from = from;
   }

   public Date getTo() {
      return to;
   }

   public void setTo(Date to) {
      this.to = to;
   }
}
