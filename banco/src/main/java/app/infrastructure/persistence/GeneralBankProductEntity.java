package app.infrastructure.persistence;

import app.domain.models.Category;
import app.domain.models.GeneralBankProduct;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "general_bank_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralBankProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String productCode;
  private String productName;

  @Enumerated(EnumType.STRING)
  private Category category;

  private boolean requiresApproval;

  public static GeneralBankProductEntity fromDomain(GeneralBankProduct product) {
    if (product == null) {
      return null;
    }
    GeneralBankProductEntity entity = new GeneralBankProductEntity();
    entity.setId(product.getId());
    entity.setProductCode(product.getProductCode());
    entity.setProductName(product.getProductName());
    entity.setCategory(product.getCategory());
    entity.setRequiresApproval(product.isRequiresApproval());
    return entity;
  }

  public GeneralBankProduct toDomain() {
    GeneralBankProduct product = new GeneralBankProduct();
    product.setId(getId());
    product.setProductCode(getProductCode());
    product.setProductName(getProductName());
    product.setCategory(getCategory());
    product.setRequiresApproval(isRequiresApproval());
    return product;
  }
}
