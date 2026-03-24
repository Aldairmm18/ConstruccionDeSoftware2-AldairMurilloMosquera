package app.application.usecases;

import app.domain.models.PersonClient;
import app.domain.models.CorporateClient;

public interface ClientManagementUseCase {
    PersonClient createNaturalClient(PersonClient client);
    CorporateClient createCorporateClient(CorporateClient client);
}
