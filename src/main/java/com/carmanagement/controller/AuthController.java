package com.carmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String registered,
                            Model model) {
        if (error != null) model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
        if (logout != null) model.addAttribute("message", "Đăng xuất thành công");
        if (registered != null) model.addAttribute("registered", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "auth/login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}
