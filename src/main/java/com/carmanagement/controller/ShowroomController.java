package com.carmanagement.controller;

import com.carmanagement.entity.Showroom;
import com.carmanagement.service.ShowroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/showrooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GIAM_DOC')")
public class ShowroomController {

    private final ShowroomService showroomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("showrooms", showroomService.findAllActive());
        return "admin/showrooms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("showroom", new Showroom());
        return "admin/showrooms/form";
    }

    @PostMapping
    public String create(@ModelAttribute("showroom") Showroom showroom, RedirectAttributes ra) {
        showroomService.save(showroom);
        ra.addFlashAttribute("success", "Thêm showroom thành công: " + showroom.getName());
        return "redirect:/admin/showrooms";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("showroom", showroomService.findById(id));
        return "admin/showrooms/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("showroom") Showroom showroom,
                         RedirectAttributes ra) {
        var existing = showroomService.findById(id);
        existing.setCode(showroom.getCode());
        existing.setName(showroom.getName());
        existing.setAddress(showroom.getAddress());
        existing.setCity(showroom.getCity());
        existing.setPhone(showroom.getPhone());
        existing.setEmail(showroom.getEmail());
        existing.setActive(showroom.getActive());
        showroomService.save(existing);
        ra.addFlashAttribute("success", "Cập nhật showroom thành công");
        return "redirect:/admin/showrooms";
    }
}
