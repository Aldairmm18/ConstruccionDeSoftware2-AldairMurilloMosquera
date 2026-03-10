package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailData {

    private String entityType;
    private String entityId;
    private String previousValue;
    private String newValue;
    private String description;
}
