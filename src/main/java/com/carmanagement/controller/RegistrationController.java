package com.carmanagement.controller;

import com.carmanagement.dto.request.UserRegistrationRequest;
import com.carmanagement.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationService registrationService;

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("registrationRequest", new UserRegistrationRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute("registrationRequest") UserRegistrationRequest request,
                              BindingResult bindingResult) {
        registrationService.register(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        return "redirect:/login?registered=true";
    }
}
