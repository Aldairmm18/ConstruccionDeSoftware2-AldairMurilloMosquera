package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.ClientEntity;
import app.application.adapters.persistence.sql.entities.CorporateClientEntity;
import app.application.adapters.persistence.sql.repositories.ClientRepository;
import app.application.adapters.persistence.sql.repositories.CorporateClientRepository;
import app.domain.models.Client;
import app.domain.models.CorporateClient;
import app.domain.models.PersonClient;
import app.domain.ports.ClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientPersistenceAdapter implements ClientPort {

    private final ClientRepository clientRepository;
    private final CorporateClientRepository corporateClientRepository;

    // ==================== SAVE ====================

    @Override
    public Client save(Client client) {
        if (client instanceof CorporateClient) {
            CorporateClientEntity entity = toCorporateEntity((CorporateClient) client);
            if (entity.getId() != null && entity.getId() == 0L) entity.setId(null);
            return toCorporateDomain(corporateClientRepository.save(entity));
        }
        ClientEntity entity = toClientEntity(client);
        if (entity.getId() != null && entity.getId() == 0L) entity.setId(null);
        return toPersonClientDomain(clientRepository.save(entity));
    }

    // ==================== FIND ====================

    @Override
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .map(this::toPersonClientDomain)
                .map(Client.class::cast)
                .orElseGet(() -> corporateClientRepository.findById(id)
                        .map(this::toCorporateDomain)
                        .orElse(null));
    }

    @Override
    public List<Client> findAll() {
        List<Client> all = new ArrayList<>();
        clientRepository.findAll().stream().map(this::toPersonClientDomain).forEach(all::add);
        corporateClientRepository.findAll().stream().map(this::toCorporateDomain).forEach(all::add);
        return all;
    }

    @Override
    public Client findByDocument(String document) {
        return clientRepository.findByDocument(document)
                .<Client>map(this::toPersonClientDomain)
                .orElseGet(() -> corporateClientRepository.findByDocument(document)
                        .map(this::toCorporateDomain)
                        .orElse(null));
    }

    // ==================== EXISTS ====================

    @Override
    public boolean existsByDocument(String document) {
        return clientRepository.existsByDocument(document)
                || corporateClientRepository.existsByDocument(document);
    }

    @Override
    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email)
                || corporateClientRepository.existsByEmail(email);
    }

    // ==================== MAPPING: PersonClient <-> ClientEntity ====================

    private ClientEntity toClientEntity(Client model) {
        ClientEntity entity = new ClientEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDocument(model.getDocument());
        entity.setEmail(model.getEmail());
        entity.setPhone(model.getPhone());
        entity.setAddress(model.getAddress());
        if (model instanceof PersonClient) {
            entity.setBirthDate(((PersonClient) model).getBirthDate());
        }
        return entity;
    }

    private PersonClient toPersonClientDomain(ClientEntity entity) {
        PersonClient model = new PersonClient();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDocument(entity.getDocument());
        model.setEmail(entity.getEmail());
        model.setPhone(entity.getPhone());
        model.setAddress(entity.getAddress());
        model.setBirthDate(entity.getBirthDate());
        return model;
    }

    // ==================== MAPPING: CorporateClient <-> CorporateClientEntity ====================

    private CorporateClientEntity toCorporateEntity(CorporateClient model) {
        CorporateClientEntity entity = new CorporateClientEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDocument(model.getDocument());
        entity.setEmail(model.getEmail());
        entity.setPhone(model.getPhone());
        entity.setAddress(model.getAddress());
        entity.setBusinessName(model.getCompanyName());
        entity.setNit(model.getNIT());
        entity.setLegalRepresentative(model.getLegalRepresentative());
        return entity;
    }

    private CorporateClient toCorporateDomain(CorporateClientEntity entity) {
        CorporateClient model = new CorporateClient();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDocument(entity.getDocument());
        model.setEmail(entity.getEmail());
        model.setPhone(entity.getPhone());
        model.setAddress(entity.getAddress());
        model.setCompanyName(entity.getBusinessName());
        model.setNIT(entity.getNit());
        model.setLegalRepresentative(entity.getLegalRepresentative());
        return model;
    }
}
