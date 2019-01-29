package se.moadb.recruitserver.presentation;

import java.sql.Timestamp;

/**
 * Represents a JSON object containing availability dates in a POST request.
 */
public class AvailabilityInPostRequest {

   private Timestamp from;
   private Timestamp to;

   public AvailabilityInPostRequest() {

   }
   public AvailabilityInPostRequest(Timestamp from, Timestamp to) {
      this.from = from;
      this.to = to;
   }

   public Timestamp getFrom() {
      return from;
   }

   public void setFrom(Timestamp from) {
      this.from = from;
   }

   public Timestamp getTo() {
      return to;
   }

   public void setTo(Timestamp to) {
      this.to = to;
   }
}
