package it.sara.demo.service.util;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {


    private final StringUtil stringUtil;

    public UserValidation(StringUtil stringUtil) {
        this.stringUtil = stringUtil;
    }

    public void validateUserData(CriteriaAddUser criteria) {

        if (stringUtil.isNullOrEmpty(criteria.getFirstName())) {
            throw new GenericException(400, "First name is required");
        }
        if (!isNameValid(criteria.getFirstName())) {
            throw new GenericException(400, "Invalid first name");
        }


        if (stringUtil.isNullOrEmpty(criteria.getLastName())) {
            throw new GenericException(400, "Last name is required");
        }
        if (!isNameValid(criteria.getLastName())) {
            throw new GenericException(400, "Invalid last name");
        }


        if (stringUtil.isNullOrEmpty(criteria.getEmail())) {
            throw new GenericException(400, "Email is required");
        }
        if (!isValidEmail(criteria.getEmail())) {
            throw new GenericException(400, "Invalid email format");
        }

        if (stringUtil.isNullOrEmpty(criteria.getPhoneNumber())) {
            throw new GenericException(400, "Phone is required");
        }
        if (!isPhoneNumberValid(criteria.getPhoneNumber())) {
            throw new GenericException(400, "Invalid phone number format");
        }

        if (containsSqlInjectionRisk(criteria.getFirstName()) ||
            containsSqlInjectionRisk(criteria.getLastName()) ||
            containsSqlInjectionRisk(criteria.getEmail())) {
            throw new GenericException(400, "Invalid characters detected");
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }


    public boolean isPhoneNumberValid(String phoneNumber) {
        String regex = "^(?:\\+39|0039)?3\\d{9}$";
        return phoneNumber != null && phoneNumber.matches(regex);
    }

    public boolean isNameValid(String value) {
        return value != null && value.matches("^[a-zA-Z ]+$");
    }


    public boolean containsSqlInjectionRisk(String value) {
        if (value == null) return false;

        String lower = value.toLowerCase();

        return lower.contains("'") ||
            lower.contains("--") ||
            lower.contains(";") ||
            lower.contains("/*") ||
            lower.contains("*/") ||
            lower.contains(" or ") ||
            lower.contains(" and ");
    }



    public void validateGetUsersCriteria(CriteriaGetUsers criteriaGetUsers) throws GenericException {
        if (criteriaGetUsers.getLimit() <= 0) {
            throw new GenericException(400, "Limit must be greater than 0");
        }

        if (criteriaGetUsers.getOffset() < 0) {
            throw new GenericException(400, "Offset must be >= 0");
        }
        if (containsSqlInjectionRisk(criteriaGetUsers.getQuery())){
            throw new GenericException(400, "Invalid characters detected");
        }
    }


}
