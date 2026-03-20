package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorporateClient extends Person {

  private String businessName;
  private String nit;
  private String legalRepresentative;
  private String username;
}
