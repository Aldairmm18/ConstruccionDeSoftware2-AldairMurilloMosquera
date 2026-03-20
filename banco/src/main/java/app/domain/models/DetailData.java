package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailData {

  private Long id;
  private String entityType;
  private Long entityId;
  private String previousValue;
  private String newValue;
  private String description;
}
