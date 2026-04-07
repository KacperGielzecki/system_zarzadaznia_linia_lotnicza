package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.gfm.system_zarzadzania_linia_lotnicza.dto.EmployeeForm;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/dodaj-pracownika")
    public String pokazFormularz(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        return "dodaj-pracownika";
    }

    @PostMapping("/dodaj-pracownika")
    public String zapiszPracownika(@ModelAttribute("employeeForm") EmployeeForm form, Model model) {
        try {
            employeeService.registerEmployee(form);
            model.addAttribute("success", "Pomyślnie dodano pracownika: " + form.getFirstName() + " " + form.getLastName());
            model.addAttribute("employeeForm", new EmployeeForm()); // Czyścimy formularz po sukcesie
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // Przekazujemy treść błędu (np. zły PESEL) do HTML
        }
        return "dodaj-pracownika";
    }
}