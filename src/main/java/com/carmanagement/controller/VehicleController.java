package com.carmanagement.controller;

import com.carmanagement.dto.request.VehicleCreateRequest;
import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.service.BrandService;
import com.carmanagement.service.CarModelService;
import com.carmanagement.service.ShowroomService;
import com.carmanagement.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/inventory/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
public class VehicleController {

    private final VehicleService vehicleService;
    private final BrandService brandService;
    private final CarModelService carModelService;
    private final ShowroomService showroomService;

    @GetMapping
    public String list(VehicleSearchRequest searchRequest, Model model) {
        model.addAttribute("vehicles", vehicleService.search(searchRequest));
        model.addAttribute("search", searchRequest);
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("showrooms", showroomService.findAllActive());
        return "inventory/vehicles/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.findWithDetailsById(id));
        return "inventory/vehicles/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createForm(Model model) {
        model.addAttribute("vehicle", new VehicleCreateRequest());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("showrooms", showroomService.findAllActive());
        return "inventory/vehicles/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String create(@Valid @ModelAttribute("vehicle") VehicleCreateRequest request,
                         BindingResult result,
                         @RequestParam(required = false) MultipartFile[] images,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandService.findAllActive());
            model.addAttribute("showrooms", showroomService.findAllActive());
            return "inventory/vehicles/form";
        }
        var vehicle = vehicleService.create(request);
        if (images != null && images.length > 0 && !images[0].isEmpty()) {
            vehicleService.uploadImages(vehicle.getId(), images);
        }
        ra.addFlashAttribute("success", "Thêm xe thành công: " + vehicle.getVin());
        return "redirect:/inventory/vehicles/" + vehicle.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String editForm(@PathVariable Long id, Model model) {
        var vehicle = vehicleService.findWithDetailsById(id);
        var request = new VehicleCreateRequest();
        request.setVin(vehicle.getVin());
        request.setCarModelId(vehicle.getCarModel().getId());
        request.setShowroomId(vehicle.getShowroom() != null ? vehicle.getShowroom().getId() : null);
        request.setColor(vehicle.getColor());
        request.setColorCode(vehicle.getColorCode());
        request.setImportPrice(vehicle.getImportPrice());
        request.setSellingPrice(vehicle.getSellingPrice());
        request.setImportDate(vehicle.getImportDate());
        request.setNotes(vehicle.getNotes());
        model.addAttribute("vehicle", request);
        model.addAttribute("vehicleId", id);
        model.addAttribute("selectedBrandId", vehicle.getCarModel().getBrand().getId());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("showrooms", showroomService.findAllActive());
        return "inventory/vehicles/form";
    }

    @GetMapping("/brands/{brandId}/models")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Map<String, Object>> modelsByBrand(@PathVariable Long brandId) {
        return carModelService.findByBrand(brandId).stream()
            .map(m -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("id", m.getId());
                entry.put("name", m.getName() + " (" + m.getYear() + ")");
                return entry;
            })
            .collect(Collectors.toList());
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("vehicle") VehicleCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandService.findAllActive());
            model.addAttribute("showrooms", showroomService.findAllActive());
            return "inventory/vehicles/form";
        }
        vehicleService.update(id, request);
        ra.addFlashAttribute("success", "Cập nhật xe thành công");
        return "redirect:/inventory/vehicles/" + id;
    }
}
