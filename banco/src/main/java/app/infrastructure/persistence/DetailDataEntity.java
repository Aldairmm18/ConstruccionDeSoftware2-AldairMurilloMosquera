package app.infrastructure.persistence;

import app.domain.models.DetailData;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detail_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailDataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String entityType;
  private Long entityId;
  private String previousValue;
  private String newValue;
  private String description;

  public static DetailDataEntity fromDomain(DetailData detailData) {
    if (detailData == null) {
      return null;
    }
    DetailDataEntity entity = new DetailDataEntity();
    entity.setId(detailData.getId());
    entity.setEntityType(detailData.getEntityType());
    entity.setEntityId(detailData.getEntityId());
    entity.setPreviousValue(detailData.getPreviousValue());
    entity.setNewValue(detailData.getNewValue());
    entity.setDescription(detailData.getDescription());
    return entity;
  }

  public DetailData toDomain() {
    DetailData detailData = new DetailData();
    detailData.setId(getId());
    detailData.setEntityType(getEntityType());
    detailData.setEntityId(getEntityId());
    detailData.setPreviousValue(getPreviousValue());
    detailData.setNewValue(getNewValue());
    detailData.setDescription(getDescription());
    return detailData;
  }
}
