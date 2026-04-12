package app.application.usecases;

import app.domain.models.PersonClient;
import app.domain.models.CorporateClient;
import app.domain.ports.ClientPort;
import app.domain.services.ClientDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientManagementUseCaseImpl implements ClientManagementUseCase {

    private final ClientPort clientPort;
    private final ClientDomainService clientDomainService;

    @Override
    @Transactional
    public PersonClient createNaturalClient(PersonClient client) {
        clientDomainService.validateClientCreation(client);
        return clientPort.save(client);
    }

    @Override
    @Transactional
    public CorporateClient createCorporateClient(CorporateClient client) {
        clientDomainService.validateCorporateClient(client);
        // TODO: crear un CorporateClientPort dedicado para evitar este cast inseguro.
        return (CorporateClient) (Object) clientPort.save((PersonClient) (Object) client);
    }

    @Override
    public PersonClient findById(Long id) {
        return clientPort.findById(id);
    }

    @Override
    public List<PersonClient> findAll() {
        return clientPort.findAll();
    }
}
