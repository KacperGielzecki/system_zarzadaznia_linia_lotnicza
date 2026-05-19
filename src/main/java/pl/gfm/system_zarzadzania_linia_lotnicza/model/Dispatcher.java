package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DISPATCHER")
public class Dispatcher extends User {
}