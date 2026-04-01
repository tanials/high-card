package it.sara.demo;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.util.StringUtil;
import it.sara.demo.service.util.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidationTest {

    @InjectMocks
    private UserValidation userValidation;
    @Mock
    private StringUtil stringUtil;

    @BeforeEach
    void setUp() {
        stringUtil = mock(StringUtil.class);
        userValidation = new UserValidation(stringUtil);

    }

    @Nested
    class ValidateUserDataTests {

        @Test
        void shouldPassWithValidData() {
            CriteriaAddUser criteria = validCriteria();

            mockAllNotEmpty();

            assertDoesNotThrow(() -> userValidation.validateUserData(criteria));
        }

        @Test
        void shouldFailWhenFirstNameIsEmpty() {
            CriteriaAddUser c = validCriteria();

            when(stringUtil.isNullOrEmpty(c.getFirstName())).thenReturn(true);

            GenericException ex = assertThrows(GenericException.class,
                () -> userValidation.validateUserData(c));

            assertEquals("Bad Request", ex.getStatus().getMessage());
        }

        @Test
        void shouldFailWhenInvalidEmail() {
            CriteriaAddUser c = validCriteria();
            c.setEmail("wrong-email");

            mockAllNotEmpty();

            GenericException ex = assertThrows(GenericException.class,
                () -> userValidation.validateUserData(c));

            assertEquals("Bad Request", ex.getStatus().getMessage());
        }

        @Test
        void shouldFailWhenInvalidPhone() {
            CriteriaAddUser c = validCriteria();
            c.setPhoneNumber("123");

            mockAllNotEmpty();

            GenericException ex = assertThrows(GenericException.class,
                () -> userValidation.validateUserData(c));

            assertEquals("Bad Request",  ex.getStatus().getMessage());
        }

        @Test
        void shouldFailOnSqlInjection() {
            CriteriaAddUser c = validCriteria();
            c.setFirstName("Mario OR 1=1");

            mockAllNotEmpty();

            GenericException ex = assertThrows(GenericException.class,
                () -> userValidation.validateUserData(c));

            assertEquals("Bad Request",  ex.getStatus().getMessage());
        }
    }


    @Nested
    class EmailValidationTests {

        @Test
        void shouldReturnTrueForValidEmail() {
            assertTrue(userValidation.isValidEmail("test@test.com"));
        }

        @Test
        void shouldReturnFalseForInvalidEmail() {
            assertFalse(userValidation.isValidEmail("test.com"));
        }

        @Test
        void shouldReturnFalseForNull() {
            assertFalse(userValidation.isValidEmail(null));
        }
    }

    @Nested
    class PhoneValidationTests {

        @Test
        void shouldAcceptItalianMobile() {
            assertTrue(userValidation.isPhoneNumberValid("3331234567"));
        }

        @Test
        void shouldAcceptWithPrefix() {
            assertTrue(userValidation.isPhoneNumberValid("+393331234567"));
        }

        @Test
        void shouldRejectInvalidPhone() {
            assertFalse(userValidation.isPhoneNumberValid("12345"));
        }
    }

    @Nested
    class NameValidationTests {

        @Test
        void shouldAcceptValidName() {
            assertTrue(userValidation.isNameValid("Mario Rossi"));
        }

        @Test
        void shouldRejectNumbers() {
            assertFalse(userValidation.isNameValid("Mario123"));
        }

        @Test
        void shouldRejectNull() {
            assertFalse(userValidation.isNameValid(null));
        }
    }

    @Nested
    class SqlInjectionTests {

        @Test
        void shouldDetectInjection() {
            assertTrue(userValidation.containsSqlInjectionRisk("test OR 1=1"));
            assertTrue(userValidation.containsSqlInjectionRisk("test; DROP"));
            assertTrue(userValidation.containsSqlInjectionRisk("test--"));
        }

        @Test
        void shouldAllowSafeString() {
            assertFalse(userValidation.containsSqlInjectionRisk("Mario"));
        }

        @Test
        void shouldAllowNull() {
            assertFalse(userValidation.containsSqlInjectionRisk(null));
        }
    }

    @Nested
    class GetUsersCriteriaTests {

        @Test
        void shouldPassValidCriteria() {
            CriteriaGetUsers c = new CriteriaGetUsers();
            c.setLimit(10);
            c.setOffset(0);

            assertDoesNotThrow(() -> userValidation.validateGetUsersCriteria(c));
        }

        @Test
        void shouldFailWhenLimitInvalid() {
            CriteriaGetUsers c = new CriteriaGetUsers();
            c.setLimit(0);
            c.setOffset(0);

            assertThrows(GenericException.class,
                () -> userValidation.validateGetUsersCriteria(c));
        }

        @Test
        void shouldFailWhenOffsetNegative() {
            CriteriaGetUsers c = new CriteriaGetUsers();
            c.setLimit(10);
            c.setOffset(-1);

            assertThrows(GenericException.class,
                () -> userValidation.validateGetUsersCriteria(c));
        }

        @Test
        void shouldFailOnSqlInjection() {
            CriteriaGetUsers c = new CriteriaGetUsers();
            c.setLimit(10);
            c.setOffset(0);
            c.setQuery("test OR 1=1");

            assertThrows(GenericException.class,
                () -> userValidation.validateGetUsersCriteria(c));
        }
    }

    private CriteriaAddUser validCriteria() {
        CriteriaAddUser c = new CriteriaAddUser();
        c.setFirstName("Mario");
        c.setLastName("Rossi");
        c.setEmail("test@test.com");
        c.setPhoneNumber("3331234567");
        return c;
    }

    private void mockAllNotEmpty() {
        when(stringUtil.isNullOrEmpty(any())).thenReturn(false);
    }
}
