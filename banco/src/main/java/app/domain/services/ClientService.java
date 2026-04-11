package app.domain.services;

import app.domain.models.PersonClient;
import app.domain.ports.ClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio Consolidado de Clientes.
 * Gestiona el ciclo de vida completo de los clientes bancarios.
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
                "Ya existe un cliente con identificación: " + client.getDocument());
        }
        
        if (clientPort.existsByEmail(client.getEmail())) {
            throw new app.domain.Exceptions.InvalidEmailException(
                "Ya existe un cliente con email: " + client.getEmail());
        }

        // Reglas de Negocio y Validaciones
        clientDomainService.validateClientCreation(client);
        
        // Persistencia Final
        return clientPort.save(client);
    }

    public PersonClient findById(Long id) {
        return clientPort.findById(id);
    }

    public List<PersonClient> findAll() {
        return clientPort.findAll();
    }
}
