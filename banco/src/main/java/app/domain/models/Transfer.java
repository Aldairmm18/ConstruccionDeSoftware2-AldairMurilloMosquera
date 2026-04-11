package app.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Transfer {

  private Long id;
  private BankAccount sourceAccount;
  private BankAccount targetAccount;
  private BigDecimal amount;
  private TransferStatus transferStatus;
  private LocalDateTime creationDate;
  private LocalDateTime approvalDate;
  
  // CORRECCIÓN 3: Reemplazo de IDs primitivos por referencias a la entidad User
  private User creatorUser;
  private User approverUser;
  
  private LocalDateTime expirationDate;

  public Transfer() {
      this.creationDate = LocalDateTime.now();
      // Límite por defecto de 60 minutos
      this.expirationDate = this.creationDate.plusMinutes(60);
      this.transferStatus = TransferStatus.PENDING;
  }

  /**
   * Verifica si la transferencia ha expirado en base a la fecha actual.
   */
  public boolean isExpired() {
      if (this.expirationDate == null) return false;
      return LocalDateTime.now().isAfter(this.expirationDate);
  }
}
