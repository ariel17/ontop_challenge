package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

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
    private Type type;

    @Column(nullable = false)
    private Operation operation;

    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "from_routing", length = 10)
    private String fromRouting;

    @Column(name = "from_account", length = 10)
    private String fromAccount;

    @Column(name = "to_routing", length = 10)
    private String toRouting;

    @Column(name = "to_account", length = 10)
    private String toAccount;

    @Column(name = "wallet_transaction_id", nullable = false)
    private Long walletTransactionId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
