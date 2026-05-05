package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Service;
import pl.gfm.system_zarzadzania_linia_lotnicza.dto.EmployeeForm;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.UserRepository;

import java.time.LocalDate;

@Service
public class EmployeeService {

    private final UserRepository userRepository;

    public EmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerEmployee(EmployeeForm form) {
        // 1. Sprawdzamy czy dane nie są puste (Imię, Nazwisko, PESEL)
        if (form.getFirstName() == null || form.getFirstName().isBlank() ||
                form.getLastName() == null || form.getLastName().isBlank() ||
                form.getPesel() == null || form.getPesel().isBlank()) {
            throw new IllegalArgumentException("Imię, nazwisko oraz PESEL są wymagane!");
        }

        // 2. Sprawdzamy czy PESEL już istnieje
        if (userRepository.existsByPesel(form.getPesel())) {
            throw new IllegalArgumentException("Pracownik o numerze PESEL " + form.getPesel() + " już istnieje w systemie!");
        }

        User newUser;
        LocalDate today = LocalDate.now();

        // 3. LOGIKA SPECYFICZNA DLA ROLI
        switch (form.getRole()) {
            case "PILOT":
                if (form.getLicenseDate() == null || form.getMedExams() == null) {
                    throw new IllegalArgumentException("Dla Pilota wymagana jest data licencji i badań!");
                }
                Pilot pilot = new Pilot();
                pilot.setMedicalExamExpiryDate(form.getMedExams());
                pilot.setLicenseExpiryDate(form.getLicenseDate());

                // DODANO (09.04): Przypisanie modeli samolotów z formularza
                pilot.setAllowedModels(form.getAllowedModels());

                newUser = pilot;

                // Jeśli licencja LUB badania wygasły -> status nieaktywny
                if (form.getLicenseDate().isBefore(today) || form.getMedExams().isBefore(today)) {
                    newUser.setActive(false);
                }
                break;

            case "STEWARDESS":
                if (form.getLicenseDate() == null || form.getMedExams() == null) {
                    throw new IllegalArgumentException("Dla Stewardess wymagana jest data licencji i badań!");
                }
                Stewardess stewardess = new Stewardess();
                stewardess.setMedicalExamExpiryDate(form.getMedExams());
                stewardess.setLicenseExpiryDate(form.getLicenseDate());
                newUser = stewardess;

                // Jeśli licencja LUB badania wygasły -> status nieaktywny
                if (form.getLicenseDate().isBefore(today) || form.getMedExams().isBefore(today)) {
                    newUser.setActive(false);
                }
                break;

            case "MECHANIC":
                // Mechanik MUSI mieć certyfikat
                if (form.getCertNumber() == null || form.getCertNumber().isBlank()) {
                    throw new IllegalArgumentException("Numer certyfikatu jest wymagany dla mechanika!");
                }
                Mechanic mechanic = new Mechanic();
                mechanic.setCertificateNumber(form.getCertNumber());
                newUser = mechanic;
                break;

            case "ADMIN":
                newUser = new Administrator();
                break;

            default:
                throw new IllegalArgumentException("Nie wybrano poprawnej roli pracownika!");
        }

        newUser.setFirstName(form.getFirstName());
        newUser.setLastName(form.getLastName());
        newUser.setPesel(form.getPesel());

        userRepository.save(newUser);
    }
}