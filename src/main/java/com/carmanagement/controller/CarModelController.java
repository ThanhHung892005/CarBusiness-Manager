package com.carmanagement.controller;

import com.carmanagement.entity.CarModel;
import com.carmanagement.enums.CarType;
import com.carmanagement.service.BrandService;
import com.carmanagement.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/models")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GIAM_DOC')")
public class CarModelController {

    private final CarModelService carModelService;
    private final BrandService brandService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) Long brandId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("brand.name", "name"));
        model.addAttribute("models", carModelService.search(brandId, null, null, pageable));
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedBrandId", brandId);
        return "admin/models/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("carModel", new CarModel());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("carTypes", CarType.values());
        return "admin/models/form";
    }

    @PostMapping
    public String create(@ModelAttribute("carModel") CarModel carModel, RedirectAttributes ra) {
        carModelService.save(carModel);
        ra.addFlashAttribute("success", "Thêm dòng xe thành công");
        return "redirect:/admin/models";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("carModel", carModelService.findById(id));
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("carTypes", CarType.values());
        return "admin/models/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("carModel") CarModel carModel,
                         RedirectAttributes ra) {
        var existing = carModelService.findById(id);
        existing.setName(carModel.getName());
        existing.setYear(carModel.getYear());
        existing.setCarType(carModel.getCarType());
        existing.setEngine(carModel.getEngine());
        existing.setTransmission(carModel.getTransmission());
        existing.setFuelType(carModel.getFuelType());
        existing.setSeats(carModel.getSeats());
        existing.setBasePrice(carModel.getBasePrice());
        existing.setDescription(carModel.getDescription());
        existing.setActive(carModel.getActive());
        existing.setBrand(carModel.getBrand());
        carModelService.save(existing);
        ra.addFlashAttribute("success", "Cập nhật dòng xe thành công");
        return "redirect:/admin/models";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        carModelService.deleteById(id);
        ra.addFlashAttribute("success", "Đã vô hiệu hóa dòng xe");
        return "redirect:/admin/models";
    }
}
