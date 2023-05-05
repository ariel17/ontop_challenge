package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.MovementType;
import ar.com.ariel17.ontop.core.domain.Operation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Entity
@Table(name = "movements")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Operation operation;

    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @OneToOne(optional = false)
    @JoinColumn(name = "ontop_account_id", referencedColumnName = "id")
    private BankAccountOwnerEntity onTopAccount;

    @OneToOne
    @JoinColumn(name = "external_account_id", referencedColumnName = "id")
    private BankAccountOwnerEntity externalAccount;

    @Column(name = "wallet_transaction_id", nullable = false)
    private Long walletTransactionId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private PaymentEntity payment;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
