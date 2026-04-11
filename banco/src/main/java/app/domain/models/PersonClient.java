package app.domain.models;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.Exceptions.InvalidNationalIdException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonClient extends Person {

  private LocalDate birthDate;

  @Override
  public void setDocument(String document) {
      if (document == null || document.isBlank()) {
          throw new InvalidNationalIdException("ID (Cédula) is required");
      }
      if (!document.matches("\\d{7,10}")) {
          throw new InvalidNationalIdException("ID (Cédula) must have between 7 and 10 digits");
      }
      super.setDocument(document);
  }
}
