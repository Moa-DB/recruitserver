package se.moadb.recruitserver.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when making an invalid POST request
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class InvalidPostRequestException extends RuntimeException {
   public InvalidPostRequestException() {
      super("You made an invalid post request.");
   }

   public InvalidPostRequestException(String key, String object) {
      super(createMessage(key,object));
   }

   public InvalidPostRequestException(String list) {
      super(createListMessage(list));
   }

   private static String createMessage(String key, String list) {
      StringBuilder sb = new StringBuilder();
      sb.append("You tried to make a POST request. It has a missing mandatory key: '");
      sb.append(key);
      sb.append("' within an object inside the '");
      sb.append(list);
      sb.append("' list.");
      return sb.toString();
   }

   private static String createListMessage(String list) {
      StringBuilder sb = new StringBuilder();
      sb.append("You tried to make a POST request. It is missing objects (empty) inside the '");
      sb.append(list);
      sb.append("' list.");
      return sb.toString();
   }

}
