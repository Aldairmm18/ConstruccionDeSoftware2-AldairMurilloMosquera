package app.application.usecases;

import app.domain.models.PersonClient;
import app.domain.models.CorporateClient;
import java.util.List;

public interface ClientManagementUseCase {
    PersonClient createNaturalClient(PersonClient client);
    CorporateClient createCorporateClient(CorporateClient client);
    PersonClient findById(Long id);
    List<PersonClient> findAll();
}
