package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.UserRepository;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String pokazStroneGlowna() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String pokazLogowanie() {
        return "logowanie";
    }

    @PostMapping("/login")
    public String zaloguj(@RequestParam String email, @RequestParam String pesel, HttpSession session, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Nieprawidłowy adres e-mail lub PESEL!");
            return "logowanie";
        }

        User user = userOpt.get();

        // Weryfikacja hasła (w naszym przypadku PESEL)
        if (!user.getPesel().equals(pesel)) {
            model.addAttribute("error", "Nieprawidłowy adres e-mail lub PESEL!");
            return "logowanie";
        }

        if (!user.isActive()) {
            model.addAttribute("error", "Twoje konto jest nieaktywne (np. wygasłe badania/licencje)!");
            return "logowanie";
        }

        // Zapisujemy zalogowanego użytkownika w sesji przeglądarki
        session.setAttribute("zalogowanyUzytkownik", user);

        // KIEROWANIE DO ODPOWIEDNIEGO PANELU NA PODSTAWIE INSTANCEOF
        if (user instanceof Administrator) {
            return "redirect:/dodaj-pracownika";
        } else if (user instanceof Dispatcher) {
            return "redirect:/kalendarz";
        } else if (user instanceof Mechanic) {
            return "redirect:/mechanik";
        } else if (user instanceof Pilot) {
            return "redirect:/zaloga";
        }

        model.addAttribute("error", "Nie rozpoznano roli użytkownika w systemie.");
        return "logowanie";
    }

    @GetMapping("/logout")
    public String wyloguj(HttpSession session) {
        session.invalidate(); // Czyszczenie sesji
        return "redirect:/login";
    }
}