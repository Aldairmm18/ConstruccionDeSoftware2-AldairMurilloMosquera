package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
public class Person {

    private long id;
    private String name;
    private String document;
    private String phone;
    private String email;
    private String address;
    
}
