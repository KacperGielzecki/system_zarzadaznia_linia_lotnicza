package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MECHANIC")
public class Mechanic extends User {

    private String certificateNumber;

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
}