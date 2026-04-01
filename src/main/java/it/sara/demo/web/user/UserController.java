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

/**
 * Controller REST responsabile della gestione delle operazioni sugli utenti.
 *
 * <p>Espone endpoint relativi a:</p>
 * <ul>
 *     <li>Creazione di un nuovo utente</li>
 *     <li>Ricerca utenti con filtri e paginazione</li>
 * </ul>
 *
 * <p>La classe utilizza:</p>
 * <ul>
 *     <li>{@link UserService} per eseguire la logica applicativa</li>
 *     <li>{@link AddUserAssembler} per convertire le richieste di creazione</li>
 *     <li>{@link GetUserAssembler} per convertire le richieste di ricerca</li>
 * </ul>
 *
 * <p>Gli endpoint sono prefissati con <b>/user</b> e seguono la versione <b>v1</b>.</p>
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddUserAssembler addUserAssembler;

    @Autowired
    private GetUserAssembler getUserAssembler;

    /**
     * Endpoint REST per la creazione di un nuovo utente.
     *
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     *     <li>Converte la richiesta {@link AddUserRequest} in {@link CriteriaAddUser} tramite {@link AddUserAssembler}</li>
     *     <li>Invoca il servizio {@link UserService#addUser(CriteriaAddUser)}</li>
     *     <li>Restituisce una risposta di successo generica tramite {@link GenericResponse}</li>
     * </ul>
     *
     * @param request oggetto contenente i dati dell’utente da creare
     * @return {@link ResponseEntity} con un messaggio di successo
     */
    @PostMapping(value = {"/v1/user"})
    public ResponseEntity<GenericResponse> addUser(@RequestBody AddUserRequest request) {
        CriteriaAddUser criteria = addUserAssembler.toCriteria(request);
        userService.addUser(criteria);
        return ResponseEntity.ok(GenericResponse.success("User added."));
    }

    /**
     * Endpoint REST per la ricerca degli utenti in base ai criteri specificati.
     *
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     *     <li>Converte la richiesta {@link GetUsersRequest} in {@link CriteriaGetUsers} tramite {@link GetUserAssembler}</li>
     *     <li>Richiede al servizio {@link UserService#getUsers(CriteriaGetUsers)} la lista filtrata</li>
     *     <li>Converte il risultato in {@link GetUsersResponse}</li>
     * </ul>
     *
     * @param request criteri di ricerca, ordinamento e paginazione
     * @return {@link ResponseEntity} contenente la risposta formattata per il client
     */
    @PostMapping(value = {"/v1/users"})
    public ResponseEntity<GetUsersResponse> getUsers(@RequestBody GetUsersRequest request) {
        CriteriaGetUsers criteriaGetUsers = getUserAssembler.toCriteria(request);
        GetUsersResult result = userService.getUsers(criteriaGetUsers);
        return ResponseEntity.ok(getUserAssembler.toResponse(result));
    }
}
