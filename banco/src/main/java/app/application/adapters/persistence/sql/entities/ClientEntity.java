package app.application.adapters.persistence.sql.entities;

import app.domain.models.PersonClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity extends PersonEntity {

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  public static ClientEntity fromDomain(PersonClient client) {
    if (client == null) {
      return null;
    }
    ClientEntity entity = new ClientEntity();
    entity.setId(client.getId());
    entity.setName(client.getName());
    entity.setDocument(client.getDocument());
    entity.setEmail(client.getEmail());
    entity.setPhone(client.getPhone());
    entity.setAddress(client.getAddress());
    entity.setBirthDate(client.getBirthDate());
    return entity;
  }

  public PersonClient toDomain() {
    PersonClient client = new PersonClient();
    client.setId(getId());
    client.setName(getName());
    client.setDocument(getDocument());
    client.setEmail(getEmail());
    client.setPhone(getPhone());
    client.setAddress(getAddress());
    client.setBirthDate(getBirthDate());
    return client;
  }
}
