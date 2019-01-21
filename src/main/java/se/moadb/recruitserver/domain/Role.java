package se.moadb.recruitserver.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Role implements GrantedAuthority {

    @Id
    @NotNull
    private String name;

    private Long id;

    public Role(){}

    public Role(String name) {
        this.name = name;
    }

    public Role(@NotNull String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
