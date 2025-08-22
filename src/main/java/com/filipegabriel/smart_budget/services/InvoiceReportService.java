package com.filipegabriel.smart_budget.services;

import com.filipegabriel.smart_budget.entities.Client;
import com.filipegabriel.smart_budget.entities.Movement;
import com.filipegabriel.smart_budget.entities.enums.MovementType;
import com.filipegabriel.smart_budget.repositories.ClientRepository;
import com.filipegabriel.smart_budget.repositories.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private FeeCalculator feeCalculator;

    public String generateClientBalanceReport(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));

        List<Movement> movements = movementRepository.findByAccountAccountId(client.getAccounts().get(0).getAccountId());

        long creditCount = movements.stream().filter(m -> m.getMovementType().name().equals("DEPOSIT")).count();
        long debitCount = movements.stream().filter(m -> !m.getMovementType().name().equals("DEPOSIT")).count();
        int totalMovements = movements.size();
        BigDecimal totalPaid = feeCalculator.calculateFee(totalMovements);

        BigDecimal initialBalance = movements.isEmpty() ? BigDecimal.ZERO : movements.get(0).getAmount();

        BigDecimal currentBalance = movements.stream()
                .skip(1)
                .map(m -> m.getMovementType() == MovementType.DEPOSIT ? m.getAmount() : m.getAmount().negate())
                .reduce(initialBalance, BigDecimal::add);

        String address = client.getAddresses().isEmpty() ? "No address" :
                client.getAddresses().get(0).getStreet() + ", " +
                        client.getAddresses().get(0).getNumber() + ", " +
                        client.getAddresses().get(0).getComplement() + ", " +
                        client.getAddresses().get(0).getNeighborhood() + ", " +
                        client.getAddresses().get(0).getCity() + ", " +
                        client.getAddresses().get(0).getState() + ", " +
                        client.getAddresses().get(0).getZipCode();

        return String.format(
                "Relatório do cliente: %s\nCliente desde: %s\nEndereço: %s\nMovimentações de crédito: %d\nMovimentações de débito: %d\nTotal de movimentações: %d\nValor pago pelas movimentações: %s\nSaldo inicial: %s\nSaldo atual: %s",
                client.getName(), client.getRegistrationDate(), address, creditCount, debitCount, totalMovements, totalPaid, initialBalance, currentBalance
        );
    }

    public String generateClientBalanceReportPeriod(Long clientId, LocalDate start, LocalDate end) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        List<Movement> movements = client.getAccounts().stream()
                .flatMap(acc -> movementRepository.findByAccountAccountId(acc.getAccountId()).stream())
                .filter(m -> !m.getMovementDate().isBefore(start) && !m.getMovementDate().isAfter(end))
                .collect(Collectors.toList());

        long creditCount = movements.stream().filter(m -> m.getMovementType().name().equals("DEPOSIT")).count();
        long debitCount = movements.stream().filter(m -> !m.getMovementType().name().equals("DEPOSIT")).count();
        int totalMovements = movements.size();
        BigDecimal totalPaid = feeCalculator.calculateFee(totalMovements);

        BigDecimal initialBalance = movements.isEmpty() ? BigDecimal.ZERO : movements.get(0).getAmount();

        BigDecimal currentBalance = movements.stream()
                .skip(1)
                .map(m -> m.getMovementType() == MovementType.DEPOSIT ? m.getAmount() : m.getAmount().negate())
                .reduce(initialBalance, BigDecimal::add);

        String address = client.getAddresses().isEmpty() ? "No address" :
                client.getAddresses().get(0).getStreet() + ", " +
                        client.getAddresses().get(0).getNumber() + ", " +
                        client.getAddresses().get(0).getComplement() + ", " +
                        client.getAddresses().get(0).getNeighborhood() + ", " +
                        client.getAddresses().get(0).getCity() + ", " +
                        client.getAddresses().get(0).getState() + ", " +
                        client.getAddresses().get(0).getZipCode();

        return String.format(
                "Relatório do cliente por periodo: %s\nPeríodo: %s a %s\nCliente desde: %s\nEndereço: %s\nMovimentações de crédito: %d\nMovimentações de débito: %d\nTotal de movimentações: %d\nValor pago pelas movimentações: %s\nSaldo inicial: %s\nSaldo atual: %s",
                client.getName(), start, end, client.getRegistrationDate(), address, creditCount, debitCount, totalMovements, totalPaid, initialBalance, currentBalance
        );
    }

    public String generateAllClientsBalanceReport(LocalDate date) {
        StringBuilder sb = new StringBuilder();
        List<Client> clients = clientRepository.findAll();

        sb.append(String.format("Relatório de saldo de todos os clientes:\n"));

        for (Client client : clients) {
            BigDecimal balance = client.getAccounts().stream()
                    .flatMap(acc -> movementRepository.findByAccountAccountId(acc.getAccountId()).stream())
                    .map(m -> m.getMovementType().name().equals("DEPOSIT") ? m.getAmount() : m.getAmount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            sb.append(String.format(
                    "Cliente: %s - Cliente desde: %s - Saldo em %s: %s\n",
                    client.getName(), client.getRegistrationDate(), date, balance
            ));
        }

        return sb.toString();
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

        return sb.toString();
    }
}