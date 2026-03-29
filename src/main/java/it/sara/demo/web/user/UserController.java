package it.sara.demo.web.user;

import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.web.assembler.AddUserAssembler;
import it.sara.demo.web.assembler.GetUserAssembler;
import it.sara.demo.web.response.GenericResponse;
import it.sara.demo.web.user.request.AddUserRequest;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.GetUsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddUserAssembler addUserAssembler;

    @Autowired
    private GetUserAssembler getUserAssembler;

    @PostMapping(value = {"/v1/user"})
    public ResponseEntity<GenericResponse> addUser(@RequestBody AddUserRequest request) {
        CriteriaAddUser criteria = addUserAssembler.toCriteria(request);
        userService.addUser(criteria);
        return ResponseEntity.ok(GenericResponse.success("User added."));
    }

    @PostMapping(value = {"/v1/users"})
    public ResponseEntity<GetUsersResponse> getUsers(@RequestBody GetUsersRequest request) {
        CriteriaGetUsers criteriaGetUsers = getUserAssembler.toCriteria(request);
        GetUsersResult result = userService.getUsers(criteriaGetUsers);
        return ResponseEntity.ok(getUserAssembler.toResponse(result));
    }
}
