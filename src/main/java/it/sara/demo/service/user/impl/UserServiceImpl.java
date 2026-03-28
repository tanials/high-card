package it.sara.demo.service.user.impl;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.StringUtil;
import it.sara.demo.service.util.UserValidation;
import it.sara.demo.web.assembler.AddUserAssembler;
import it.sara.demo.web.assembler.GetUserAssembler;
import it.sara.demo.web.assembler.UserAssembler;
import it.sara.demo.web.user.request.AddUserRequest;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.GetUsersResponse;
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

    @Autowired
    private AddUserAssembler addUserAssembler;

    @Autowired
    private GetUserAssembler getUserAssembler;

    @Override
    public AddUserResult addUser(AddUserRequest request) throws GenericException {
        CriteriaAddUser criteria = addUserAssembler.toCriteria(request);
        AddUserResult returnValue;
        User user;

        try {

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

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw new GenericException(GenericException.GENERIC_ERROR);
        }
        return returnValue;
    }

    @Override
    public GetUsersResponse getUsers(GetUsersRequest request) throws GenericException {
        CriteriaGetUsers criteriaGetUsers = getUserAssembler.toCriteria(request);
        try {

            userValidation.validateGetUsersCriteria(criteriaGetUsers);

            // 2. PRENDO DATI (simula DB)
            List<User> users = userRepository.getAll();

            if(CollectionUtils.isEmpty(users)){
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

            List<UserDTO> pagedUsers = users.stream()
                    .map(userAssembler::toDTO)
                    .collect(Collectors.toList()).subList(start, end);

            GetUsersResponse response = new GetUsersResponse();
            response.setTotal(total);
            response.setUsers(pagedUsers);

            return response;

        } catch (Exception e) {
            throw new GenericException(GenericException.GENERIC_ERROR);
        }
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
