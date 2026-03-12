package app.domain.models;

import java.sql.Date;
import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Client extends Person {

    private ArrayList<BankAccount> bankAccounts;
    private Date birthDate;

}
