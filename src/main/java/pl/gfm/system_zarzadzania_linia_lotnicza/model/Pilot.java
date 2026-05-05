package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("PILOT")
public class Pilot extends User {

    private LocalDate medicalExamExpiryDate;
    private LocalDate licenseExpiryDate;

    private String allowedModels;

    public LocalDate getMedicalExamExpiryDate() {
        return medicalExamExpiryDate;
    }

    public void setMedicalExamExpiryDate(LocalDate medicalExamExpiryDate) {
        this.medicalExamExpiryDate = medicalExamExpiryDate;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getAllowedModels() {
        return allowedModels;
    }

    public void setAllowedModels(String allowedModels) {
        this.allowedModels = allowedModels;
    }
}