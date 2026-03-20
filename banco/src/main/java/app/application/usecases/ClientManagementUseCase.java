package app.application.usecases;

import app.domain.models.Client;
import app.domain.models.CorporateClient;

public interface ClientManagementUseCase {
    Client createNaturalClient(Client client);
    CorporateClient createCorporateClient(CorporateClient client);
}
