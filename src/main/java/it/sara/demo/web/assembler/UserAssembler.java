package it.sara.demo.web.assembler;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.service.database.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler {

    public UserDTO toDTO(User user) {
        UserDTO returnValue = new UserDTO();
        returnValue.setGuid(user.getGuid());
        returnValue.setFirstName(user.getFirstName());
        returnValue.setLastName(user.getLastName());
        returnValue.setEmail(user.getEmail());
        returnValue.setPhoneNumber(user.getPhoneNumber());
        return returnValue;
    }
}
