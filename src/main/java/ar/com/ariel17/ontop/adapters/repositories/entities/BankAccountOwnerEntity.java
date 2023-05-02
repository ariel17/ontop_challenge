package ar.com.ariel17.ontop.adapters.repositories.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Currency;
import java.util.Date;

@Entity
@Table(name = "bank_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountOwnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Long routing;

    @Column(nullable = false)
    private Long account;

    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(name = "id_number", nullable = false, length = 15)
    private String idNumber;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;
}
