package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeneralBankProduct {

    private String productCode;
    private String productName;
    private Category Category;
    private boolean requiresApproval;
}
