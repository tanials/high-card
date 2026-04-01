package it.sara.demo.service.util;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Classe responsabile della validazione dei dati relativi agli utenti.
 *
 * <p>Questa componente fornisce diversi metodi di utilità per:</p>
 * <ul>
 *     <li>Validare i dati necessari alla creazione di un nuovo utente</li>
 *     <li>Verificare la correttezza di indirizzi email, numeri di telefono e nomi</li>
 *     <li>Individuare possibili pattern riconducibili a tentativi di SQL Injection</li>
 *     <li>Validare i criteri di ricerca degli utenti (limit, offset, query)</li>
 * </ul>
 *
 * <p>La classe è annotata con {@code @Component} per consentire l'iniezione automatica
 * all'interno dei servizi che necessitano di logiche di validazione.</p>
 */
@Slf4j
@Component
public class UserValidation {

    private final StringUtil stringUtil;

    /**
     * Costruttore che inizializza la classe con una utility per la gestione delle stringhe.
     *
     * @param stringUtil utility per controlli base sulle stringhe (null, vuote, ecc.)
     */
    public UserValidation(StringUtil stringUtil) {
        this.stringUtil = stringUtil;
    }


    /**
     * Valida i dati necessari per la creazione di un nuovo utente tramite {@link CriteriaAddUser}.
     *
     * <p>Il metodo verifica:</p>
     * <ul>
     *     <li>Presenza e correttezza di nome e cognome</li>
     *     <li>Validità del formato email</li>
     *     <li>Validità del numero di telefono</li>
     *     <li>Assenza di pattern riconducibili a SQL injection</li>
     * </ul>
     *
     * @param criteria oggetto contenente i dati dell’utente da creare
     * @throws GenericException se uno qualsiasi dei dati risulta mancante, non valido o potenzialmente dannoso
     */
    public void validateUserData(CriteriaAddUser criteria) {

        if (stringUtil.isNullOrEmpty(criteria.getFirstName())) {
            log.error("First name is missing");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
        if (!isNameValid(criteria.getFirstName())) {
            log.error("Invalid first name format: {}", criteria.getFirstName());
            throw new GenericException(GenericException.BAD_REQUEST);
        }

        if (stringUtil.isNullOrEmpty(criteria.getLastName())) {
            log.error("Last name is missing");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
        if (!isNameValid(criteria.getLastName())) {
            log.error("Invalid last name format: {}", criteria.getLastName());
            throw new GenericException(GenericException.BAD_REQUEST);
        }

        if (stringUtil.isNullOrEmpty(criteria.getEmail())) {
            log.error("Email is missing");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
        if (!isValidEmail(criteria.getEmail())) {
            log.error("Invalid email format: {}", criteria.getEmail());
            throw new GenericException(GenericException.BAD_REQUEST);
        }

        if (stringUtil.isNullOrEmpty(criteria.getPhoneNumber())) {
            log.error("Phone number is missing");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
        if (!isPhoneNumberValid(criteria.getPhoneNumber())) {
            log.error("Invalid phone number format: {}", criteria.getPhoneNumber());
            throw new GenericException(GenericException.BAD_REQUEST);
        }

        if (containsSqlInjectionRisk(criteria.getFirstName()) ||
            containsSqlInjectionRisk(criteria.getLastName()) ||
            containsSqlInjectionRisk(criteria.getEmail())) {
            log.error("Invalid characters detected in user data");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
    }


    /**
     * Verifica se un indirizzo email rispetta un formato valido.
     *
     * @param email indirizzo email da verificare
     * @return {@code true} se l’email è valida, {@code false} altrimenti
     */
    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Verifica se un numero di telefono è valido.
     *
     * <p>Il formato è basato sugli standard italiani:</p>
     * <ul>
     *     <li>Può contenere prefisso internazionale +39 o 0039</li>
     *     <li>Deve iniziare con «3» (numero mobile)</li>
     *     <li>Deve contenere 10 cifre totali dopo il prefisso</li>
     * </ul>
     *
     * @param phoneNumber numero di telefono da validare
     * @return {@code true} se il numero è valido, {@code false} altrimenti
     */
    public boolean isPhoneNumberValid(String phoneNumber) {
        String regex = "^(?:\\+39|0039)?3\\d{9}$";
        return phoneNumber != null && phoneNumber.matches(regex);
    }

    /**
     * Verifica se il nome/cognome contiene solo caratteri alfabetici e spazi.
     *
     * @param value stringa da verificare
     * @return {@code true} se valida, altrimenti {@code false}
     */
    public boolean isNameValid(String value) {
        return value != null && value.matches("^[a-zA-Z ]+$");
    }


    /**
     * Individua potenziali pattern riconducibili a tentativi di SQL Injection.
     *
     * <p>I controlli comprendono sequenze tipiche come:</p>
     * <ul>
     *     <li>'</li>
     *     <li>--</li>
     *     <li>;</li>
     *     <li>sequenza: "*" + "/" o sequenza: "/" + "*"</li>
     *     <li>" or "</li>
     *     <li>" and "</li>
     * </ul>
     *
     * @param value stringa da analizzare
     * @return {@code true} se sono rilevati pattern rischiosi, {@code false} altrimenti
     */
    public boolean containsSqlInjectionRisk(String value) {
        if (value == null) {
            return false;
        }

        String lower = value.toLowerCase();

        return lower.contains("'") ||
            lower.contains("--") ||
            lower.contains(";") ||
            lower.contains("/*") ||
            lower.contains("*/") ||
            lower.contains(" or ") ||
            lower.contains(" and ");
    }


    /**
     * Valida i parametri utilizzati nella ricerca utenti tramite {@link CriteriaGetUsers}.
     *
     * <p>Il metodo verifica che:</p>
     * <ul>
     *     <li>{@code limit} sia maggiore di zero</li>
     *     <li>{@code offset} sia maggiore o uguale a zero</li>
     *     <li>la query di ricerca non presenti rischi di SQL injection</li>
     * </ul>
     *
     * @param criteriaGetUsers parametri di ricerca
     * @throws GenericException se uno dei parametri non è valido
     */
    public void validateGetUsersCriteria(CriteriaGetUsers criteriaGetUsers) throws GenericException {
        if (criteriaGetUsers.getLimit() <= 0) {
            log.error("Limit must be greater than 0");
            throw new GenericException(GenericException.BAD_REQUEST);
        }

        if (criteriaGetUsers.getOffset() < 0) {
            log.error("Offset must be >= 0");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
        if (containsSqlInjectionRisk(criteriaGetUsers.getQuery())) {
            log.error("Invalid characters detected");
            throw new GenericException(GenericException.BAD_REQUEST);
        }
    }


}
