package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.gfm.system_zarzadzania_linia_lotnicza.dto.EmployeeForm;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.Administrator;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.User;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/dodaj-pracownika")
    public String pokazFormularz(HttpSession session, Model model) {

        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Administrator)) {
            return "redirect:/login?error=BrakDostepu";
        }

        model.addAttribute("employeeForm", new EmployeeForm());
        return "dodaj-pracownika";
    }

    @PostMapping("/dodaj-pracownika")
    public String zapiszPracownika(@ModelAttribute("employeeForm") EmployeeForm form, HttpSession session, Model model) {
        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Administrator)) {
            return "redirect:/login?error=BrakDostepu";
        }

        try {
            employeeService.registerEmployee(form);
            model.addAttribute("success", "Pomyślnie dodano pracownika: " + form.getFirstName() + " " + form.getLastName());
            model.addAttribute("employeeForm", new EmployeeForm());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "dodaj-pracownika";
    }
}