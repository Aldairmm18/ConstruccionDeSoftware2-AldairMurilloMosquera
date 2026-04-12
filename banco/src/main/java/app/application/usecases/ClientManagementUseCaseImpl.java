package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.DuplicateIdentificationException;
import app.domain.Exceptions.InvalidEmailException;
import app.domain.models.*;
import app.domain.ports.ClientRepository;
import app.domain.ports.OperationsLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientManagementUseCaseImpl implements ClientManagementUseCase {

    private final ClientRepository clientRepository;
    private final OperationsLogRepository operationsLogRepository;

    @Override
    @Transactional
    public PersonClient registerNaturalPerson(PersonClient client) {
        if (clientRepository.existsByIdentification(client.getIdentification())) {
            throw new DuplicateIdentificationException("Document already registered: " + client.getIdentification());
        }
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new InvalidEmailException("Email already registered: " + client.getEmail());
        }

        PersonClient saved = clientRepository.save(client);
        registerLog("NATURAL_PERSON_REGISTERED", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public CorporateClient registerCorporateCompany(CorporateClient company) {
        if (clientRepository.existsByIdentification(company.getIdentification())) {
            throw new DuplicateIdentificationException("NIT already registered: " + company.getIdentification());
        }
        if (clientRepository.existsByEmail(company.getEmail())) {
            throw new InvalidEmailException("Email already registered: " + company.getEmail());
        }

        CorporateClient saved = clientRepository.save(company);
        registerLog("CORPORATE_CLIENT_REGISTERED", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Client updateContactInfo(String clientId, String address, String phone, String email) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Client not found"));

        if (!client.getEmail().equals(email) && clientRepository.existsByEmail(email)) {
            throw new InvalidEmailException("Email already taken: " + email);
        }

        client.setAddress(address);
        client.setPhone(phone);
        client.setEmail(email);

        Client updated = clientRepository.save(client);
        registerLog("CLIENT_CONTACT_UPDATED", clientId);
        return updated;
    }

    @Override
    public Optional<Client> findByIdentification(String doc) {
        return clientRepository.findByIdentification(doc);
    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) (Object) clientRepository.findAll();
    }

    private void registerLog(String operation, String clientId) {
        OperationsLog log = new OperationsLog();
        log.setId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setOperation(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("clientId", clientId);
        log.setDetails(details);
        
        operationsLogRepository.save(log);
    }
}
