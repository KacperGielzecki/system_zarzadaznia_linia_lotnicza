package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("STEWARDESS")
public class Stewardess extends User {

    private LocalDate medicalExamExpiryDate;
    private LocalDate licenseExpiryDate;

    public LocalDate getMedicalExamExpiryDate() { return medicalExamExpiryDate; }
    public void setMedicalExamExpiryDate(LocalDate medicalExamExpiryDate) { this.medicalExamExpiryDate = medicalExamExpiryDate; }
    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }
}