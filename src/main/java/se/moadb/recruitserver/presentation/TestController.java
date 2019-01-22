package se.moadb.recruitserver.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.moadb.recruitserver.application.RoleService;
import se.moadb.recruitserver.domain.Role;


@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    RoleService roleService;

    @GetMapping
    public Role getRole(){
        return roleService.findByName("recruit");
    }

}
