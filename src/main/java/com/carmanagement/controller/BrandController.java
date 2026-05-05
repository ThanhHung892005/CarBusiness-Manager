package com.carmanagement.controller;

import com.carmanagement.entity.Brand;
import com.carmanagement.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        model.addAttribute("brands", brandService.findAll(keyword, PageRequest.of(page, 15)));
        model.addAttribute("keyword", keyword);
        return "admin/brands/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "admin/brands/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Brand brand,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) return "admin/brands/form";
        brandService.save(brand);
        ra.addFlashAttribute("success", "Thêm hãng xe thành công");
        return "redirect:/admin/brands";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("brand", brandService.findById(id));
        return "admin/brands/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Brand brand,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) return "admin/brands/form";
        brand.setId(id);
        brandService.save(brand);
        ra.addFlashAttribute("success", "Cập nhật hãng xe thành công");
        return "redirect:/admin/brands";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        brandService.deleteById(id);
        ra.addFlashAttribute("success", "Đã vô hiệu hóa hãng xe");
        return "redirect:/admin/brands";
    }
}
