package it.sara.demo.service.user.impl;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.service.assembler.UserAssembler;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.UserValidation;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserValidation userValidation;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAssembler userAssembler;

    @Override
    public AddUserResult addUser(CriteriaAddUser criteria)  throws GenericException{

        AddUserResult returnValue;
        User user;

        returnValue = new AddUserResult();

        //Validazione dei dati in ingresso per evitare di inserire dati non corretti nel database e validazione per SQL Injection
        userValidation.validateUserData(criteria);

        user = new User();
        user.setFirstName(criteria.getFirstName());
        user.setLastName(criteria.getLastName());
        user.setEmail(criteria.getEmail());
        user.setPhoneNumber(criteria.getPhoneNumber());

        if (!userRepository.save(user)) {
            throw new GenericException(500, "Error saving user");
        }

        return returnValue;
    }

    @Override
    public GetUsersResult getUsers(CriteriaGetUsers criteriaGetUsers) throws GenericException{

        userValidation.validateGetUsersCriteria(criteriaGetUsers);

        // 2. PRENDO DATI (simula DB)
        List<User> users = userRepository.getAll();

        if (CollectionUtils.isEmpty(users)) {
            throw new GenericException(GenericException.NOT_FOUND);
        }

        if (criteriaGetUsers.getQuery() != null && !criteriaGetUsers.getQuery().trim().isEmpty()) {
            String query = criteriaGetUsers.getQuery().toLowerCase();

            users = users.stream()
                .filter(u ->
                    (userValidation.isNameValid(query) && u.getFirstName().toLowerCase().contains(query)) ||
                        (userValidation.isNameValid(query) && u.getLastName().toLowerCase().contains(query)) ||
                        (userValidation.isValidEmail(query) && u.getEmail().toLowerCase().contains(query)) ||
                        (userValidation.isPhoneNumberValid(query) && u.getPhoneNumber().contains(query))
                )
                .collect(Collectors.toList());
        }

        users.sort(getComparator(criteriaGetUsers.getOrder()));

        int total = users.size();

        // 6. PAGINATION
        int start = Math.min(criteriaGetUsers.getOffset(), total);
        int end = Math.min(start + criteriaGetUsers.getLimit(), total);

        List<UserDTO> pagedUsers = users.subList(start, end).stream()
            .map(userAssembler::toDTO).toList();

        GetUsersResult response = new GetUsersResult();
        response.setTotal(total);
        response.setUsers(pagedUsers);

        return response;

    }

    private Comparator<User> getComparator(CriteriaGetUsers.OrderType order) {

        if (order == null) {
            return Comparator.comparing(User::getFirstName);
        }
        return switch (order) {
            case BY_FIRSTNAME -> Comparator.comparing(User::getFirstName);
            case BY_FIRSTNAME_DESC -> Comparator.comparing(User::getFirstName).reversed();
            case BY_LASTNAME -> Comparator.comparing(User::getLastName);
            case BY_LASTNAME_DESC -> Comparator.comparing(User::getLastName).reversed();
        };
    }
}
