package it.sara.demo;


import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.service.assembler.UserAssembler;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.impl.UserServiceImpl;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.UserValidation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest { @Mock
private UserValidation userValidation;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAssembler userAssembler;

    @InjectMocks
    private UserServiceImpl userService;


    @Nested
    class AddUserTests {

        @Test
        void shouldAddUserSuccessfully() {

            CriteriaAddUser criteria = buildValidCriteria();

            when(userRepository.save(any(User.class))).thenReturn(true);

            AddUserResult result = userService.addUser(criteria);

            assertNotNull(result);
            verify(userValidation).validateUserData(criteria);
            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenSaveFails() {
            CriteriaAddUser criteria = buildValidCriteria();

            when(userRepository.save(any(User.class))).thenReturn(false);


            GenericException ex = assertThrows(GenericException.class,
                () -> userService.addUser(criteria));

            assertEquals(500, ex.getStatus().getCode());
        }
    }

    @Nested
    class GetUsersTests {

        @Test
        void shouldReturnUsersWithoutFilter() {

            CriteriaGetUsers criteria = baseCriteria();

            List<User> users = new ArrayList<>(List.of(
                buildUser("Mario", "Rossi"),
                buildUser("Anna", "Bianchi")
            ));

            when(userRepository.getAll()).thenReturn(users);
            mockAssembler();

            GetUsersResult result = userService.getUsers(criteria);

            assertEquals(2, result.getTotal());
            assertEquals(2, result.getUsers().size());
            verify(userValidation).validateGetUsersCriteria(criteria);
        }

        @Test
        void shouldThrowNotFoundWhenNoUsers() {
            CriteriaGetUsers criteria = baseCriteria();

            when(userRepository.getAll()).thenReturn(Collections.emptyList());

            assertThrows(GenericException.class,
                () -> userService.getUsers(criteria));
        }

        @Test
        void shouldFilterByFirstName() {
            CriteriaGetUsers criteria = baseCriteria();
            criteria.setQuery("mar");

            List<User> users = new ArrayList<>(List.of(
                buildUser("Mario", "Rossi"),
                buildUser("Anna", "Bianchi")
            ));

            when(userRepository.getAll()).thenReturn(users);

            when(userValidation.isNameValid(any())).thenReturn(true);
            when(userValidation.isValidEmail(any())).thenReturn(false);
            when(userValidation.isPhoneNumberValid(any())).thenReturn(false);

            mockAssembler();

            GetUsersResult result = userService.getUsers(criteria);

            assertEquals(1, result.getUsers().size());
        }

        @Test
        void shouldSortByLastNameDesc() {
            CriteriaGetUsers criteria = baseCriteria();
            criteria.setOrder(CriteriaGetUsers.OrderType.BY_LASTNAME_DESC);

            List<User> users = new ArrayList<>(List.of(
                buildUser("Mario", "Bianchi"),
                buildUser("Anna", "Rossi")
            ));

            when(userRepository.getAll()).thenReturn(users);
            mockAssemblerWithLastName();

            GetUsersResult result = userService.getUsers(criteria);

            assertEquals("Rossi", result.getUsers().get(0).getLastName());
        }

        @Test
        void shouldApplyPagination() {
            CriteriaGetUsers criteria = baseCriteria();
            criteria.setLimit(1);
            criteria.setOffset(1);
            criteria.setOrder(CriteriaGetUsers.OrderType.BY_FIRSTNAME);

            List<User> users = new ArrayList<>(List.of(
                buildUser("Mario", "Rossi"),
                buildUser("Anna", "Bianchi")
            ));

            when(userRepository.getAll()).thenReturn(users);
            mockAssembler();

            GetUsersResult result = userService.getUsers(criteria);

            assertEquals(2, result.getTotal());
            assertEquals(1, result.getUsers().size());
        }
    }


    private CriteriaAddUser buildValidCriteria() {
        CriteriaAddUser criteria = new CriteriaAddUser();
        criteria.setFirstName("Mario");
        criteria.setLastName("Rossi");
        criteria.setEmail("mario@test.com");
        criteria.setPhoneNumber("3331234567");
        return criteria;
    }

    private CriteriaGetUsers baseCriteria() {
        CriteriaGetUsers criteria = new CriteriaGetUsers();
        criteria.setLimit(10);
        criteria.setOffset(0);
        return criteria;
    }

    private User buildUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail("test@test.com");
        user.setPhoneNumber("3331234567");
        return user;
    }

    private void mockAssembler() {
        when(userAssembler.toDTO(any(User.class))).thenReturn(new UserDTO());
    }

    private void mockAssemblerWithLastName() {
        when(userAssembler.toDTO(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserDTO dto = new UserDTO();
            dto.setLastName(u.getLastName());
            return dto;
        });
    }
}
