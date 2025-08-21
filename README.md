# SmartBudget

**SmartBudget** is a financial management application designed to help users track and optimize their budgets effectively.

## Features

- Manage and track financial budgets.
- Store data using H2 or Oracle databases.
- RESTful API for integration with other systems.
- Built with Spring Boot for rapid development.

## Technologies

- **Java 8**
- **Spring Boot 2.5.14**
  - Spring Data JPA
  - Spring Web
- **H2 Database** (runtime)
- **Oracle JDBC Driver** (runtime)
- **Lombok** (for reducing boilerplate code)
- **Maven** (build tool)

## Prerequisites

- Java 1.8 or higher
- Maven 3.6 or higher
- (Optional) Oracle Database for production use

## Architecture

```mermaid
classDiagram
    class ClientType {
        <<enum>>
        PF
        PJ
    }

    class MovementType {
        <<enum>>
        DEPOSIT
        WITHDRAW
        TRANSFER
        FEE
    }

    class Client {
        - Long clientId
        - String name
        - String cpfCnpj
        - String email
        - ClientType type
        - LocalDate registrationDate
        - List~Account~ accounts
        - List~Address~ addresses
    }

    class Address {
        - Long addressId
        - String street
        - String number
        - String complement
        - String neighborhood
        - String city
        - String state
        - String zipCode
        - Client client
    }

    class Account {
        - Long accountId
        - String accountNumber
        - BigDecimal balance
        - LocalDate creationDate
        - Boolean active
        - Client client
        - Bank bank
        - List~Movement~ movements
    }

    class Movement {
        - Long movementId
        - LocalDate movementDate
        - BigDecimal amount
        - MovementType type
        - String description
        - Account account
    }

    class Bank {
        - Long bankId
        - String name
        - List~Account~ account
    }

    class Invoice {
        - Long invoiceId
        - LocalDate startDate
        - LocalDate endDate
        - BigDecimal totalValue
        - Client client
        - List~Movement~ movements
    }

    class FeeCalculator {
        + BigDecimal calculateFee(Movement movement, Client client)
    }

    %% Relacionamentos
    Client "1" --> "0..*" Account
    Client "1" --> "0..*" Invoice
    Client "1" --> "0..*" Address
    Account "1" --> "0..*" Movement
    Bank "1" --> "0..*" Account
    Invoice "1" --> "0..*" Movement
```

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/smart-budget.git
   cd smart-budget
