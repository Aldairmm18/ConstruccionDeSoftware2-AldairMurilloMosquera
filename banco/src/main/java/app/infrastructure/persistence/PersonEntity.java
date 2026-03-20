package app.infrastructure.persistence;

import app.domain.models.Person;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String document;
  private String email;
  private String phone;
  private String address;

  public static PersonEntity fromDomain(Person person) {
    if (person == null) {
      return null;
    }
    PersonEntity entity = new PersonEntity();
    entity.setId(person.getId());
    entity.setName(person.getName());
    entity.setDocument(person.getDocument());
    entity.setEmail(person.getEmail());
    entity.setPhone(person.getPhone());
    entity.setAddress(person.getAddress());
    return entity;
  }

  public Person toDomain() {
    Person person = new Person();
    person.setId(getId());
    person.setName(getName());
    person.setDocument(getDocument());
    person.setEmail(getEmail());
    person.setPhone(getPhone());
    person.setAddress(getAddress());
    return person;
  }
}
