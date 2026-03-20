package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralBankProduct {

  private Long id;
  private String productCode;
  private String productName;
  private Category category;
  private boolean requiresApproval;
}
