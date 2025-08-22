package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.entities.Invoice;
import com.filipegabriel.smart_budget.entities.Movement;
import com.filipegabriel.smart_budget.entities.enums.MovementType;
import com.filipegabriel.smart_budget.repositories.ClientRepository;
import com.filipegabriel.smart_budget.repositories.InvoiceRepository;
import com.filipegabriel.smart_budget.repositories.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceReportService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private FeeCalculator feeCalculator;

    @Value("${reports.folder}")
    private String reportsFolder;

    public String generateClientBalanceReport(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        List<Movement> movements = movementRepository.findByAccountAccountId(
                client.getAccounts().get(0).getAccountId()
        );

        long creditCount = movements.stream().filter(m -> m.getMovementType() == MovementType.DEPOSIT).count();
        long debitCount = movements.stream().filter(m -> m.getMovementType() != MovementType.DEPOSIT).count();
        int totalMovements = movements.size();
        BigDecimal totalPaid = feeCalculator.calculateFee(totalMovements);

        BigDecimal initialBalance = movements.isEmpty() ? BigDecimal.ZERO : movements.get(0).getAmount();

        BigDecimal currentBalance = movements.stream()
                .skip(1)
                .map(m -> m.getMovementType() == MovementType.DEPOSIT ? m.getAmount() : m.getAmount().negate())
                .reduce(initialBalance, BigDecimal::add);

        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setStartDate(LocalDate.now());
        invoice.setEndDate(LocalDate.now());
        invoice.setFeeAmount(totalPaid);
        invoice.setMovementCount((long) totalMovements);

        invoice = invoiceRepository.save(invoice);

        for (Movement m : movements) {
            m.setInvoice(invoice);
        }
        movementRepository.saveAll(movements);

        String address = client.getAddresses().isEmpty() ? "No address" :
                client.getAddresses().get(0).getStreet() + ", " +
                        client.getAddresses().get(0).getStreetNumber() + ", " +
                        client.getAddresses().get(0).getComplement() + ", " +
                        client.getAddresses().get(0).getNeighborhood() + ", " +
                        client.getAddresses().get(0).getCity() + ", " +
                        client.getAddresses().get(0).getState() + ", " +
                        client.getAddresses().get(0).getZipCode();

        String report = String.format(
                "Relatório do cliente: %s\nCliente desde: %s\nEndereço: %s\n" +
                        "Movimentações de crédito: %d\nMovimentações de débito: %d\n" +
                        "Total de movimentações: %d\nValor pago pelas movimentações: %s\n" +
                        "Saldo inicial: %s\nSaldo atual: %s",
                client.getName(), client.getRegistrationDate(), address, creditCount, debitCount, totalMovements, totalPaid, initialBalance, currentBalance
        );

        saveReportToFile(report, "relatorio_cliente_" + client.getName() + ".txt");

        return report;
    }

    public String generateClientBalanceReportPeriod(Long clientId, LocalDate start, LocalDate end) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        List<Movement> movements = client.getAccounts().stream()
                .flatMap(acc -> movementRepository.findByAccountAccountId(acc.getAccountId()).stream())
                .filter(m -> !m.getMovementDate().isBefore(start) && !m.getMovementDate().isAfter(end))
                .collect(Collectors.toList());

        long creditCount = movements.stream().filter(m -> m.getMovementType() == MovementType.DEPOSIT).count();
        long debitCount = movements.stream().filter(m -> m.getMovementType() != MovementType.DEPOSIT).count();
        int totalMovements = movements.size();
        BigDecimal totalPaid = feeCalculator.calculateFee(totalMovements);

        BigDecimal initialBalance = movements.isEmpty() ? BigDecimal.ZERO : movements.get(0).getAmount();

        BigDecimal currentBalance = movements.stream()
                .skip(1)
                .map(m -> m.getMovementType() == MovementType.DEPOSIT ? m.getAmount() : m.getAmount().negate())
                .reduce(initialBalance, BigDecimal::add);

        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setStartDate(start);
        invoice.setEndDate(end);
        invoice.setFeeAmount(totalPaid);
        invoice.setMovementCount((long) totalMovements);

        invoice = invoiceRepository.save(invoice);

        for (Movement m : movements) {
            m.setInvoice(invoice);
        }
        movementRepository.saveAll(movements);

        String address = client.getAddresses().isEmpty() ? "No address" :
                client.getAddresses().get(0).getStreet() + ", " +
                        client.getAddresses().get(0).getStreetNumber() + ", " +
                        client.getAddresses().get(0).getComplement() + ", " +
                        client.getAddresses().get(0).getNeighborhood() + ", " +
                        client.getAddresses().get(0).getCity() + ", " +
                        client.getAddresses().get(0).getState() + ", " +
                        client.getAddresses().get(0).getZipCode();

        String report = String.format(
                "Relatório do cliente por período: %s\nPeríodo: %s a %s\nCliente desde: %s\nEndereço: %s\n" +
                        "Movimentações de crédito: %d\nMovimentações de débito: %d\n" +
                        "Total de movimentações: %d\nValor pago pelas movimentações: %s\n" +
                        "Saldo inicial: %s\nSaldo atual: %s",
                client.getName(), start, end, client.getRegistrationDate(), address, creditCount, debitCount, totalMovements, totalPaid, initialBalance, currentBalance
        );

        saveReportToFile(report, "relatorio_de_cliente_" + client.getName() + "_por_periodo.txt");

        return report;
    }

    public String generateAllClientsBalanceReport(LocalDate date) {
        StringBuilder sb = new StringBuilder();
        List<Client> clients = clientRepository.findAll();

        sb.append("Relatório de saldo de todos os clientes:\n");

        for (Client client : clients) {
            BigDecimal balance = client.getAccounts().stream()
                    .flatMap(acc -> movementRepository.findByAccountAccountId(acc.getAccountId()).stream())
                    .map(m -> m.getMovementType() == MovementType.DEPOSIT ? m.getAmount() : m.getAmount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            sb.append(String.format(
                    "Cliente: %s - Cliente desde: %s - Saldo em %s: %s\n",
                    client.getName(), client.getRegistrationDate(), date, balance
            ));
        }

        String report = sb.toString();
        saveReportToFile(report, "relatorio_com_todos_clientes.txt");

        return report;
    }

    public String generateCompanyRevenueReport(LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder();
        List<Client> clients = clientRepository.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        sb.append(String.format("Relatório de receita da empresa (XPTO): %s a %s\n", start, end));

        for (Client client : clients) {
            List<Movement> movements = client.getAccounts().stream()
                    .flatMap(acc -> movementRepository.findByAccountAccountId(acc.getAccountId()).stream())
                    .filter(m -> !m.getMovementDate().isBefore(start) && !m.getMovementDate().isAfter(end))
                    .collect(Collectors.toList());

            long movementCount = movements.size();
            BigDecimal revenue = feeCalculator.calculateFee((int) movementCount);
            totalRevenue = totalRevenue.add(revenue);

            sb.append(String.format(
                    "Cliente: %s - Quantidade de movimentações: %d - Valor das movimentações: %s\n",
                    client.getName(), movementCount, revenue
            ));
        }

        sb.append(String.format("Total de receitas: %s", totalRevenue));

        String report = sb.toString();
        saveReportToFile(report, "relatorio_de_receita_xpto.txt");

        return report;
    }

    private void saveReportToFile(String content, String baseFileName) {
        try {
            Path folderPath = Paths.get(reportsFolder);
            Files.createDirectories(folderPath);

            String fileName = generateUniqueFileName(folderPath, baseFileName);
            Path filePath = folderPath.resolve(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW)) {
                writer.write(content);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar relatório em arquivo", e);
        }
    }

    private String generateUniqueFileName(Path folderPath, String baseFileName) {
        String fileName = baseFileName;
        Path filePath = folderPath.resolve(fileName);
        int count = 1;

        while (Files.exists(filePath)) {
            int dotIndex = baseFileName.lastIndexOf('.');
            if (dotIndex == -1) {
                fileName = baseFileName + "(" + count + ")";
            } else {
                String name = baseFileName.substring(0, dotIndex);
                String extension = baseFileName.substring(dotIndex);
                fileName = name + "(" + count + ")" + extension;
            }
            filePath = folderPath.resolve(fileName);
            count++;
        }

        return fileName;
    }
}