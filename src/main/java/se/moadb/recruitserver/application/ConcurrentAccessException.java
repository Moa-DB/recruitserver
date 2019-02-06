package se.moadb.recruitserver.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when trying to change an entity which is being changed by someone else at the same time.
 */
@ResponseStatus(value=HttpStatus.CONFLICT)
public class ConcurrentAccessException extends RuntimeException {
   public ConcurrentAccessException() {
      super("You tried to access a resource that was being accessed simultaneously by another user, which is not allowed.");
   }
   public ConcurrentAccessException(String table, String key) {
      super(createMessage(table, key));
   }
   private static String createMessage(String table, String key) {
      StringBuilder sb = new StringBuilder();
      sb.append("You tried to access the key '");
      sb.append(key);
      sb.append("' in the table '");
      sb.append(table.toString());
      sb.append("' at the same time another user was updating it. This is not allowed, and your transaction was aborted.");
      return sb.toString();
   }
}
