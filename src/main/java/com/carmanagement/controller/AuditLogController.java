package com.carmanagement.controller;

import com.carmanagement.enums.AuditAction;
import com.carmanagement.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/audit-logs")
@PreAuthorize("hasRole('GIAM_DOC')")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String username,
                       @RequestParam(required = false) AuditAction action,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        LocalDateTime from = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime to   = LocalDateTime.of(2099, 12, 31, 23, 59);

        var pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        var logs = auditLogRepository.search(username, action, from, to, pageable);

        model.addAttribute("logs", logs);
        model.addAttribute("username", username);
        model.addAttribute("selectedAction", action);
        model.addAttribute("actions", AuditAction.values());
        return "admin/audit-logs/list";
    }
}
