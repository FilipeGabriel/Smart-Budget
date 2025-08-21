package com.filipegabriel.smart_budget.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.filipegabriel.smart_budget.entities.enums.ClientType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long clientId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cpfCnpj;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "client")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Account> accounts = new ArrayList<>();

}
