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

        // 2. Walidacja PESEL: Musi składać się z dokładnie 11 cyfr
        if (!form.getPesel().matches("\\d{11}")) {
            throw new IllegalArgumentException("Numer PESEL musi składać się z dokładnie 11 cyfr!");
        }

        // 3. Walidacja E-mail: Jeśli wpisany, musi zawierać '@' oraz poprawną domenę
        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
            if (!form.getEmail().matches(emailRegex)) {
                throw new IllegalArgumentException("Błędny format adresu e-mail! Wymagany znak '@' oraz poprawna domena.");
            }
        }

        if (userRepository.existsByPesel(form.getPesel())) {
            throw new IllegalArgumentException("Pracownik o numerze PESEL " + form.getPesel() + " już istnieje w systemie!");
        }

        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            if (userRepository.existsByEmail(form.getEmail())) {
                throw new IllegalArgumentException("Pracownik o adresie e-mail " + form.getEmail() + " już istnieje w systemie!");
            }
        }

        User newUser;
        LocalDate today = LocalDate.now();

        switch (form.getRole()) {
            case "PILOT":
                if (form.getMedExams() == null || form.getLicenseDate() == null) {
                    throw new IllegalArgumentException("Dla Pilota wymagana jest data licencji i badań!");
                }
                Pilot pilot = new Pilot();
                pilot.setMedicalExamExpiryDate(form.getMedExams());
                pilot.setLicenseExpiryDate(form.getLicenseDate());
                pilot.setAllowedModels(form.getAllowedModels());
                newUser = pilot;

                if (form.getLicenseDate().isBefore(today) || form.getMedExams().isBefore(today)) {
                    newUser.setActive(false);
                }
                break;

            case "STEWARDESS":
                if (form.getMedExams() == null || form.getLicenseDate() == null) {
                    throw new IllegalArgumentException("Dla Stewardess wymagana jest data licencji i badań!");
                }
                Stewardess stewardess = new Stewardess();
                stewardess.setMedicalExamExpiryDate(form.getMedExams());
                stewardess.setLicenseExpiryDate(form.getLicenseDate());
                newUser = stewardess;

                if (form.getLicenseDate().isBefore(today) || form.getMedExams().isBefore(today)) {
                    newUser.setActive(false);
                }
                break;

            case "MECHANIC":
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

            case "DYSPOZYTOR":
                // ZMIANA: Teraz tworzymy dedykowany obiekt klasy Dispatcher
                newUser = new Dispatcher();
                break;

            default:
                throw new IllegalArgumentException("Nie wybrano poprawnej roli pracownika!");
        }

        newUser.setFirstName(form.getFirstName());
        newUser.setLastName(form.getLastName());
        newUser.setPesel(form.getPesel());
        newUser.setEmail(form.getEmail());

        userRepository.save(newUser);
    }
}