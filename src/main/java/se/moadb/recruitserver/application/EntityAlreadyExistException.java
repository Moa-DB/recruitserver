package se.moadb.recruitserver.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when trying to add an entity with an id that already exists in the database.
 */
@ResponseStatus(value= HttpStatus.CONFLICT)
public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException() {
        super("You tried to add an entity to the database which already exists.");
    }
    public EntityAlreadyExistException(String table, Object id) {
        super(createMessage(table, id));
    }
    private static String createMessage(String table, Object id) {
        StringBuilder sb = new StringBuilder();
        sb.append("You tried to add a/n '");
        sb.append(table);
        sb.append("' with an id of '");
        sb.append(id.toString());
        sb.append("'. This id already exists in the database.");
        return sb.toString();
    }
}
