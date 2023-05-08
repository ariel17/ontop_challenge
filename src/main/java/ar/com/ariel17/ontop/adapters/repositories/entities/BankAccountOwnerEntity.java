package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.BankAccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;
import java.util.Date;

/**
 * The bank account model representation on storage.
 */
@Entity
@Table(name = "bank_accounts",
        uniqueConstraints = {@UniqueConstraint(
                name = "user_id_routing_account",
                columnNames = {"user_id", "routing", "account"}
        )})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountOwnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 10)
    private String routing;

    @Column(nullable = false, length = 10)
    private String account;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private BankAccountType type;

    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(name = "id_number", length = 15)
    private String idNumber;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
