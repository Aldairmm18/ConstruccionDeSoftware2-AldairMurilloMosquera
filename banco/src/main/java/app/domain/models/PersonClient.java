package app.domain.models;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.Exceptions.CedulaInvalidaException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonClient extends Person {

  private LocalDate birthDate;

  @Override
  public void setDocument(String document) {
      if (document == null || document.isBlank()) {
          throw new CedulaInvalidaException("Cédula es obligatoria");
      }
      if (!document.matches("\\d{7,10}")) {
          throw new CedulaInvalidaException("Cédula debe tener entre 7 y 10 dígitos");
      }
      super.setDocument(document);
  }
}
