package pl.gfm.system_zarzadzania_linia_lotnicza.dto;

import java.time.LocalDate;

public class EmployeeForm {
    private String firstName;
    private String lastName;
    private String pesel;
    private String email; // NOWE POLE
    private String role;
    private LocalDate medExams;
    private LocalDate licenseDate;
    private String certNumber;

    // NOWE POLE (Zadanie 09.04)
    private String allowedModels;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    // Getter i Setter dla Email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCertNumber() { return certNumber; }
    public void setCertNumber(String certNumber) { this.certNumber = certNumber; }
    public LocalDate getMedExams() { return medExams; }
    public void setMedExams(LocalDate medExams) { this.medExams = medExams; }
    public LocalDate getLicenseDate() { return licenseDate; }
    public void setLicenseDate(LocalDate licenseDate) { this.licenseDate = licenseDate; }
    public String getAllowedModels() { return allowedModels; }
    public void setAllowedModels(String allowedModels) { this.allowedModels = allowedModels; }
}