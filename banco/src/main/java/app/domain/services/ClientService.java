package app.domain.services;

import app.domain.models.PersonClient;
import app.domain.ports.ClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consolidated Client Service.
 * Manages the full lifecycle of banking clients.
 */
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientPort clientPort;
    private final ClientDomainService clientDomainService;

    @Transactional
    public PersonClient registerClient(PersonClient client) {
        if (clientPort.existsByDocument(client.getDocument())) {
            throw new app.domain.Exceptions.DuplicateIdentificationException(
                "A client with this identification already exists: " + client.getDocument());
        }
        
        if (clientPort.existsByEmail(client.getEmail())) {
            throw new app.domain.Exceptions.InvalidEmailException(
                "A client with this email already exists: " + client.getEmail());
        }

        // Business Rules & Validations
        clientDomainService.validateClientCreation(client);
        
        // Final Persistence
        return clientPort.save(client);
    }

    public PersonClient findById(Long id) {
        return clientPort.findById(id);
    }

    public List<PersonClient> findAll() {
        return clientPort.findAll();
    }
}
