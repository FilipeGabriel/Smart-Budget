package com.filipegabriel.smart_budget.resources;

import com.filipegabriel.smart_budget.services.InvoiceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/v1/api/reports")
public class InvoiceReportResource {

    @Autowired
    private InvoiceReportService reportService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<String> clientBalance(@PathVariable Long clientId) {
        String report = reportService.generateClientBalanceReport(clientId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/client/{clientId}/period")
    public ResponseEntity<String> clientBalancePeriod(@PathVariable Long clientId, @RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String report = reportService.generateClientBalanceReportPeriod(clientId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/all-clients")
    public ResponseEntity<String> allClientsBalance(@RequestParam String date) {
        LocalDate reportDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String report = reportService.generateAllClientsBalanceReport(reportDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/company-revenue")
    public ResponseEntity<String> companyRevenue(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String report = reportService.generateCompanyRevenueReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }
}