package se.moadb.recruitserver.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when trying to get an id of an entity which does not exist
 */
@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class EntityDoesNotExistException extends RuntimeException {
   public EntityDoesNotExistException() {
      super("You tried to access an entity from the database which does not exist.");
   }
   public EntityDoesNotExistException(String table, Object id) {
      super(createMessage(table, id));
   }
   private static String createMessage(String table, Object id) {
      StringBuilder sb = new StringBuilder();
      sb.append("You tried to access a/n '");
      sb.append(table);
      sb.append("' with an id of '");
      sb.append(id.toString());
      sb.append("'. This id could not be found in the database.");
      return sb.toString();
   }
}