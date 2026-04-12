package app.application.usecases;

import app.domain.models.Client;
import app.domain.models.CorporateClient;
import app.domain.models.PersonClient;
import java.util.List;
import java.util.Optional;

public interface ClientManagementUseCase {
    PersonClient registerNaturalPerson(PersonClient client);
    CorporateClient registerCorporateCompany(CorporateClient company);
    Client updateContactInfo(String clientId, String address, String phone, String email);
    Optional<Client> findByIdentification(String doc);
    List<Client> findAll();
}
