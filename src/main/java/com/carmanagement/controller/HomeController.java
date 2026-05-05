package com.carmanagement.controller;

import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.service.BrandService;
import com.carmanagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VehicleService vehicleService;
    private final BrandService brandService;

    @GetMapping("/home")
    public String home(Model model) {
        VehicleSearchRequest req = new VehicleSearchRequest();
        req.setStatus(VehicleStatus.AVAILABLE);
        req.setSize(8);
        model.addAttribute("featuredVehicles", vehicleService.search(req).getContent());
        model.addAttribute("brands", brandService.findAllActive());
        return "home";
    }
}
