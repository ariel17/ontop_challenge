package ar.com.ariel17.adapters.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountOwnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer userId;

    private Long routing;

    private Long account;

    private Currency currency;

    private String idNumber;

    private String firstName;

    private String lastName;

    private Date createdAt;
}
