package app.application.usecases;

import app.domain.models.Client;
import app.domain.models.CorporateClient;
import app.domain.ports.ClientPort;
import app.domain.services.ClientDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientManagementUseCaseImpl implements ClientManagementUseCase {

    private final ClientPort clientPort;
    private final ClientDomainService clientDomainService;

    @Override
    public Client createNaturalClient(Client client) {
        clientDomainService.validateClientCreation(client);
        return clientPort.save(client);
    }

    @Override
    public CorporateClient createCorporateClient(CorporateClient client) {
        // A placeholder for now
        throw new UnsupportedOperationException("No CorporateClient port configured.");
    }
}
