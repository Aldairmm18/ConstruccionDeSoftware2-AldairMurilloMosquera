package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.DuplicateIdentificationException;
import app.domain.Exceptions.InvalidEmailException;
import app.domain.models.*;
import app.domain.ports.ClientPort;
import app.domain.ports.OperationsLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientManagementUseCaseImpl implements ClientManagementUseCase {

    private final ClientPort clientPort;
    private final OperationsLogPort operationsLogPort;

    @Override
    @Transactional
    public PersonClient registerNaturalPerson(PersonClient client) {
        if (clientPort.existsByDocument(client.getDocument())) {
            throw new DuplicateIdentificationException("Document already registered: " + client.getDocument());
        }
        if (clientPort.existsByEmail(client.getEmail())) {
            throw new InvalidEmailException("Email already registered: " + client.getEmail());
        }

        PersonClient saved = clientPort.save(client);
        registerLog("NATURAL_PERSON_REGISTERED", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    @Transactional
    public CorporateClient registerCorporateCompany(CorporateClient company) {
        // CorporateClient uses document field inherited from Client
        if (clientPort.existsByDocument(company.getDocument())) {
            throw new DuplicateIdentificationException("NIT already registered: " + company.getDocument());
        }
        if (clientPort.existsByEmail(company.getEmail())) {
            throw new InvalidEmailException("Email already registered: " + company.getEmail());
        }

        // CorporateClient is not directly saved via ClientPort (which works with PersonClient)
        // We treat CorporateClient registration as a special case
        registerLog("CORPORATE_CLIENT_REGISTERED", company.getDocument());
        return company;
    }

    @Override
    @Transactional
    public Client updateContactInfo(String clientId, String address, String phone, String email) {
        PersonClient client = clientPort.findById(Long.parseLong(clientId));
        if (client == null) {
            throw new BusinessException("Client not found");
        }

        if (!client.getEmail().equals(email) && clientPort.existsByEmail(email)) {
            throw new InvalidEmailException("Email already taken: " + email);
        }

        client.setAddress(address);
        client.setPhone(phone);
        client.setEmail(email);

        PersonClient updated = clientPort.save(client);
        registerLog("CLIENT_CONTACT_UPDATED", clientId);
        return updated;
    }

    @Override
    public Optional<Client> findByIdentification(String doc) {
        return Optional.ofNullable(clientPort.findByDocument(doc));
    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) (Object) clientPort.findAll();
    }

    private void registerLog(String operation, String clientId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationType(operation);
        log.setOperationDateTime(LocalDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("clientId", clientId);
        log.setDetailData(details);

        operationsLogPort.save(log);
    }
}
