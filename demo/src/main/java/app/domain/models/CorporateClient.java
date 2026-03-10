package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CorporateClient extends Client {

    private String businessName;
    private String nit;
    private String legalRepresentative;
    private String username;
}
