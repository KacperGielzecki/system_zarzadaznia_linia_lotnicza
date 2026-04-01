package pl.gfm.system_zarzadzania_linia_lotnicza;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    // Ta metoda sprawi, że po wpisaniu adresu /dodaj-pracownika w przeglądarce,
    // Spring Boot wyświetli Twój plik HTML.
    @GetMapping("/dodaj-pracownika")
    public String pokazFormularz() {
        return "dodaj-pracownika"; // Nazwa pliku HTML bez końcówki .html
    }
}