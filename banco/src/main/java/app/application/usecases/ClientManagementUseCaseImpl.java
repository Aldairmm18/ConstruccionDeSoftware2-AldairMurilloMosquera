package app.application.usecases;

import app.domain.models.*;
import app.domain.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientManagementUseCaseImpl implements ClientManagementUseCase {

    private final ClientService clientService;

    @Override
    @Transactional
    public PersonClient registerNaturalPerson(PersonClient client) {
        return clientService.saveNaturalPerson(client);
    }

    @Override
    @Transactional
    public CorporateClient registerCorporateCompany(CorporateClient company) {
        return clientService.saveCorporateCompany(company);
    }

    @Override
    @Transactional
    public Client updateContactInfo(String clientId, String address, String phone, String email) {
        return clientService.updateContactInfo(Long.parseLong(clientId), address, phone, email);
    }

    @Override
    public Optional<Client> findByIdentification(String doc) {
        return Optional.ofNullable(clientService.findByDocument(doc));
    }

    @Override
    public List<Client> findAll() {
        return clientService.findAll();
    }
}
