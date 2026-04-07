package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {
    // Administrator dziedziczy wszystko z User, nie potrzebuje nowych pól
}