package it.sara.demo.service.user.impl;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.service.assembler.UserAssembler;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.criteria.CriteriaGetUsers.OrderType;
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


/**Implementazione del servizio utenti definito dall’interfaccia {@link UserService}.
    *
    * <p>Questa classe gestisce tutte le operazioni legate agli utenti, tra cui:</p>
    * <ul>
 *     <li>Creazione di nuovi utenti</li>
    *     <li>Ricerca utenti con filtri, ordinamento e paginazione</li>
    *     <li>Conversione tra entità e DTO tramite {@link UserAssembler}</li>
    *     <li>Validazione dei dati in ingresso tramite {@link UserValidation}</li>
    *     <li>Interazione con il livello repository tramite {@link UserRepository}</li>
    * </ul>
    *
    * <p>L’implementazione fa uso dell’annotazione Spring {@code @Service} e del logging
 * tramite Lombok {@code @Slf4j}. Le dipendenze sono iniettate tramite {@code @Autowired}.</p>
    */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserValidation userValidation;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAssembler userAssembler;


    /**
     * Aggiunge un nuovo utente al sistema utilizzando i dati forniti tramite
     * {@link CriteriaAddUser}.
     *
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     *     <li>Validazione di tutti i dati in ingresso tramite
     *     {@code userValidation.validateUserData(criteria)}, inclusi controlli per
     *     formati errati e tentativi di SQL injection.</li>
     *     <li>Creazione di un nuovo oggetto {@link User} e popolamento dei campi:
     *         <ul>
     *             <li>firstName</li>
     *             <li>lastName</li>
     *             <li>email</li>
     *             <li>phoneNumber</li>
     *         </ul>
     *     </li>
     *     <li>Persistenza dell’utente tramite {@code userRepository.save(user)}.</li>
     *     <li>Lancio di una {@link GenericException} se il salvataggio fallisce.</li>
     * </ul>
     *
     * <p>Se l’operazione va a buon fine, viene restituito un oggetto
     * {@link AddUserResult} vuoto (espandibile per includere informazioni aggiuntive
     * come l'ID generato, se necessario).</p>
     *
     * @param criteria i dati necessari alla creazione del nuovo utente
     * @return un oggetto {@link AddUserResult} che rappresenta l’esito dell’operazione
     * @throws GenericException se i dati non sono validi o se il salvataggio nel repository fallisce
     */

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


    /**
     * Recupera una lista di utenti applicando validazioni, filtri testuali, ordinamento e paginazione,
     * restituendo il risultato incapsulato in {@link GetUsersResult}.
     *
     * <p>Il processo esegue le seguenti operazioni:</p>
     * <ul>
     *     <li>Validazione dei criteri tramite {@code userValidation.validateGetUsersCriteria()}.</li>
     *     <li>Lettura di tutti gli utenti dal {@link UserRepository}.</li>
     *     <li>Lancio di {@link GenericException} con codice {@code NOT_FOUND} se non esistono utenti.</li>
     *     <li>Applicazione di un filtro opzionale sulla query di ricerca, che può essere:
     *         <ul>
     *             <li>nome (firstName)</li>
     *             <li>cognome (lastName)</li>
     *             <li>email</li>
     *             <li>numero di telefono</li>
     *         </ul>
     *         La validità dei formati è verificata tramite metodi dedicati in {@code userValidation}.
     *     </li>
     *     <li>Ordinamento attraverso un {@link Comparator} ottenuto via {@code getComparator()}.</li>
     *     <li>Paginazione basata su {@code offset} e {@code limit} presenti nei criteri.</li>
     *     <li>Conversione degli utenti filtrati/paginati in {@link UserDTO} tramite {@code userAssembler}.</li>
     * </ul>
     *
     * @param criteriaGetUsers criteri utilizzati per la ricerca, il filtraggio, l’ordinamento e la paginazione
     * @return un oggetto {@link GetUsersResult} contenente il totale degli utenti trovati e la pagina richiesta
     * @throws GenericException se i criteri di input non sono validi o se nessun utente è presente nel sistema
     */

    @Override
    public GetUsersResult getUsers(CriteriaGetUsers criteriaGetUsers) throws GenericException{

        userValidation.validateGetUsersCriteria(criteriaGetUsers);

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

        int start = Math.min(criteriaGetUsers.getOffset(), total);
        int end = Math.min(start + criteriaGetUsers.getLimit(), total);

        List<UserDTO> pagedUsers = users.subList(start, end).stream()
            .map(userAssembler::toDTO).toList();

        GetUsersResult response = new GetUsersResult();
        response.setTotal(total);
        response.setUsers(pagedUsers);

        return response;

    }


    /**
     * Restituisce un {@link Comparator} per oggetti {@link User} in base al criterio
     * di ordinamento specificato tramite {@link OrderType}.
     *
     * <p>Se il parametro {@code order} è {@code null}, il metodo fornisce come default
     * un ordinamento crescente sul campo {@code firstName}.</p>
     *
     * <p>I criteri gestiti sono:</p>
     * <ul>
     *     <li>{@code BY_FIRSTNAME} – ordina per nome in ordine crescente</li>
     *     <li>{@code BY_FIRSTNAME_DESC} – ordina per nome in ordine decrescente</li>
     *     <li>{@code BY_LASTNAME} – ordina per cognome in ordine crescente</li>
     *     <li>{@code BY_LASTNAME_DESC} – ordina per cognome in ordine decrescente</li>
     * </ul>
     *
     * @param order il tipo di ordinamento desiderato; se {@code null}, usa l’ordinamento
     *              crescente per nome
     * @return un {@code Comparator<User>} configurato secondo il criterio richiesto
     */

    private Comparator<User> getComparator(OrderType order) {

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
