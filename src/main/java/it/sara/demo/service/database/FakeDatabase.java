package it.sara.demo.service.database;

import it.sara.demo.service.database.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe rappresenta un database fittizio per scopi di test e dimostrazione. Contiene una lista statica di utenti che simula una tabella del database.
 * La classe è progettata per essere utilizzata come fonte di dati in memoria, evitando la necessità di connettersi a un database reale durante lo sviluppo e i test.
 */
public class FakeDatabase {

    public static final List<User> TABLE_USER = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setGuid(java.util.UUID.randomUUID().toString());
            user.setFirstName("First name " + i);
            user.setLastName("Last name " + i);
            user.setEmail("user" + i + "@example.com");
            user.setPhoneNumber("+39" + i);
            TABLE_USER.add(user);
        }
    }

    private FakeDatabase() {

    }

}
