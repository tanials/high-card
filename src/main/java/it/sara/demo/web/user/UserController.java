package it.sara.demo.web.user;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.user.UserService;
import it.sara.demo.web.response.GenericResponse;
import it.sara.demo.web.user.request.AddUserRequest;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.GetUsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/v1/user"}, method = RequestMethod.PUT)
    public ResponseEntity<GenericResponse> addUser(@RequestBody AddUserRequest request) throws GenericException {
        userService.addUser(request);
        return ResponseEntity.ok(GenericResponse.success("User added."));
    }

    @RequestMapping(value = {"/v1/user"}, method = RequestMethod.POST)
    public ResponseEntity<GetUsersResponse> getUsers(@RequestBody GetUsersRequest request) throws GenericException {
        return ResponseEntity.ok(userService.getUsers(request));
    }
}
