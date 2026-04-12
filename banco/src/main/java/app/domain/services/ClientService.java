package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for CLIENT MANAGEMENT
 * Handles: Registration, updates, queries, and validations for clients
 */
@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private AuditService auditService;
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Registers a new natural person client
     */
    @Transactional
    public PersonClient createNaturalPerson(
            String idNumber,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String address,
            String phone,
            String email) {
        
        validateUniqueIdentification(idNumber);
        validateUniqueEmail(email);
        
        PersonClient person = new PersonClient();
        person.setId(UUID.randomUUID().toString());
        person.setIdentification(idNumber);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setBirthDate(birthDate);
        person.setAddress(address);
        person.setPhone(phone);
        person.setEmail(email);
        
        PersonClient saved = clientRepository.save(person);
        auditService.logOperation("NATURAL_PERSON_REGISTERED", saved.getId());
        
        return saved;
    }
    
    /**
     * Registers a new company client
     */
    @Transactional
    public CorporateClient createCompany(
            String nit,
            String companyName,
            String legalRepresentative,
            String address,
            String phone,
            String email) {
        
        validateUniqueIdentification(nit);
        validateUniqueEmail(email);
        validateNIT(nit);
        
        CorporateClient company = new CorporateClient();
        company.setId(UUID.randomUUID().toString());
        company.setIdentification(nit);
        company.setNit(nit);
        company.setCompanyName(companyName);
        company.setLegalRepresentative(legalRepresentative);
        company.setAddress(address);
        company.setPhone(phone);
        company.setEmail(email);
        
        CorporateClient saved = clientRepository.save(company);
        auditService.logOperation("CORPORATE_CLIENT_REGISTERED", saved.getId());
        
        return saved;
    }
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Updates client contact information
     */
    @Transactional
    public Client updateContactInfo(
            String clientId,
            String newAddress,
            String newPhone,
            String newEmail) {
        
        Client client = findByIdOrThrow(clientId);
        
        if (!client.getEmail().equals(newEmail)) {
            validateUniqueEmail(newEmail);
        }
        
        client.setAddress(newAddress);
        client.setPhone(newPhone);
        client.setEmail(newEmail);
        
        Client updated = clientRepository.save(client);
        auditService.logOperation("CLIENT_CONTACT_UPDATED", clientId);
        
        return updated;
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    /**
     * Finds client by ID
     */
    public Client findByIdOrThrow(String id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(
                "Cliente no encontrado: " + id));
    }
    
    public Optional<Client> findById(String id) {
        return clientRepository.findById(id);
    }
    
    public Optional<Client> findByIdentification(String identification) {
        return clientRepository.findByIdentification(identification);
    }
    
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
    
    public List<Client> findAll() {
        return clientRepository.findAll();
    }
    
    // ==================== VALIDATION METHODS ====================
    
    private void validateUniqueIdentification(String identification) {
        if (clientRepository.existsByIdentification(identification)) {
            throw new DuplicateIdentificationException(
                "Ya existe un cliente con identificación: " + identification);
        }
    }
    
    private void validateUniqueEmail(String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new InvalidEmailException(
                "Ya existe un cliente con email: " + email);
        }
    }
    
    private void validateNIT(String nit) {
        if (!nit.matches("\\d{9}-\\d")) {
            throw new InvalidNitException("NIT debe tener formato XXXXXXXXX-X");
        }
        
        String number = nit.substring(0, 9);
        int dv = Character.getNumericValue(nit.charAt(nit.length() - 1));
        
        if (!calculateNitVerificationDigit(number, dv)) {
            throw new InvalidNitException("Dígito verificador de NIT inválido");
        }
    }
    
    private boolean calculateNitVerificationDigit(String nit, int expectedDv) {
        int[] weights = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};
        int sum = 0;
        String reversedNit = new StringBuilder(nit).reverse().toString();
        for (int i = 0; i < reversedNit.length(); i++) {
            int digit = Character.getNumericValue(reversedNit.charAt(i));
            sum += digit * weights[i];
        }
        int remainder = sum % 11;
        int calculatedDv = (remainder > 1) ? (11 - remainder) : remainder;
        return calculatedDv == expectedDv;
    }
}
