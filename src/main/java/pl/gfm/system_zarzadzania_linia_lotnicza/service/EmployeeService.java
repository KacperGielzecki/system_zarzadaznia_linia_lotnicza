package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Service;
import pl.gfm.system_zarzadzania_linia_lotnicza.dto.EmployeeForm;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.UserRepository;

@Service
public class EmployeeService {

    private final UserRepository userRepository;

    public EmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerEmployee(EmployeeForm form) {
        // PROGRAMOWANIE DEFENSYWNE: Sprawdzamy czy PESEL już istnieje
        if (userRepository.existsByPesel(form.getPesel())) {
            throw new IllegalArgumentException("Pracownik o numerze PESEL " + form.getPesel() + " już istnieje w systemie!");
        }

        User newUser;

        // Tworzymy odpowiedni obiekt na podstawie roli z formularza
        switch (form.getRole()) {
            case "PILOT":
                Pilot pilot = new Pilot();
                pilot.setMedicalExamExpiryDate(form.getMedExams());
                pilot.setLicenseExpiryDate(form.getLicenseDate());
                newUser = pilot;
                break;
            case "STEWARDESS":
                Stewardess stewardess = new Stewardess();
                stewardess.setMedicalExamExpiryDate(form.getMedExams());
                stewardess.setLicenseExpiryDate(form.getLicenseDate());
                newUser = stewardess;
                break;
            case "MECHANIC":
                Mechanic mechanic = new Mechanic();
                mechanic.setCertificateNumber(form.getCertNumber());
                newUser = mechanic;
                break;
            case "ADMIN":
                newUser = new Administrator();
                break;
            default:
                throw new IllegalArgumentException("Nieznana rola pracownika!");
        }

        // Uzupełniamy wspólne dane
        newUser.setFirstName(form.getFirstName());
        newUser.setLastName(form.getLastName());
        newUser.setPesel(form.getPesel());

        userRepository.save(newUser);
    }
}