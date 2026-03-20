package app.application.adapters.persistence.sql.entities;

import app.domain.models.CorporateClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "corporate_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorporateClientEntity extends PersonEntity {

  @Column(name = "business_name", nullable = false)
  private String businessName;

  @Column(name = "nit", unique = true, nullable = false)
  private String nit;

  @Column(name = "legal_representative")
  private String legalRepresentative;

  @Column(name = "username", unique = true)
  private String username;

  public static CorporateClientEntity fromDomain(CorporateClient corporateClient) {
    if (corporateClient == null) {
      return null;
    }
    CorporateClientEntity entity = new CorporateClientEntity();
    entity.setId(corporateClient.getId());
    entity.setName(corporateClient.getName());
    entity.setDocument(corporateClient.getDocument());
    entity.setEmail(corporateClient.getEmail());
    entity.setPhone(corporateClient.getPhone());
    entity.setAddress(corporateClient.getAddress());
    entity.setBusinessName(corporateClient.getBusinessName());
    entity.setNit(corporateClient.getNit());
    entity.setLegalRepresentative(corporateClient.getLegalRepresentative());
    entity.setUsername(corporateClient.getUsername());
    return entity;
  }

  public CorporateClient toDomain() {
    CorporateClient corporateClient = new CorporateClient();
    corporateClient.setId(getId());
    corporateClient.setName(getName());
    corporateClient.setDocument(getDocument());
    corporateClient.setEmail(getEmail());
    corporateClient.setPhone(getPhone());
    corporateClient.setAddress(getAddress());
    corporateClient.setBusinessName(getBusinessName());
    corporateClient.setNit(getNit());
    corporateClient.setLegalRepresentative(getLegalRepresentative());
    corporateClient.setUsername(getUsername());
    return corporateClient;
  }
}
