package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.ClientPort;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for CLIENT MANAGEMENT
 * Handles: Registration, updates, queries, and validations for clients
 */
public class ClientService {

    private final ClientPort clientPort;
    private final AuditService auditService;

    public ClientService(ClientPort clientPort, AuditService auditService) {
        this.clientPort = clientPort;
        this.auditService = auditService;
    }

    // ==================== CREATE OPERATIONS ====================

    /**
     * Registers a new natural person client
     */
    public PersonClient createNaturalPerson(
            String idNumber,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String address,
            String phone,
            String email) {

        validateUniqueDocument(idNumber);
        validateUniqueEmail(email);

        PersonClient person = new PersonClient();
        person.setId(null);
        person.setDocument(idNumber);
        person.setName(firstName);
        person.setLastName(lastName);
        person.setBirthDate(birthDate);
        person.setAddress(address);
        person.setPhone(phone);
        person.setEmail(email);
        person.setClientStatus(ClientStatus.ACTIVE);

        Client saved = clientPort.save(person);
        auditService.logOperation("NATURAL_PERSON_REGISTERED", saved.getId() != null ? saved.getId().toString() : null);

        return (PersonClient) saved;
    }

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Updates client contact information
     */
    public PersonClient updateContactInfo(
            Long clientId,
            String newAddress,
            String newPhone,
            String newEmail) {

        PersonClient client = findByIdOrThrow(clientId);

        if (!client.getEmail().equals(newEmail)) {
            validateUniqueEmail(newEmail);
        }

        client.setAddress(newAddress);
        client.setPhone(newPhone);
        client.setEmail(newEmail);

        Client updated = clientPort.save(client);
        auditService.logOperation("CLIENT_CONTACT_UPDATED", clientId.toString());

        return (PersonClient) updated;
    }

    // ==================== QUERY OPERATIONS ====================

    public PersonClient findByIdOrThrow(Long id) {
        Client client = clientPort.findById(id);
        if (client == null) {
            throw new UserNotFoundException("Cliente no encontrado: " + id);
        }
        return (PersonClient) client;
    }

    public Client findById(Long id) {
        return clientPort.findById(id);
    }

    public Client findByDocument(String document) {
        return clientPort.findByDocument(document);
    }

    public List<Client> findAll() {
        return clientPort.findAll();
    }

    // ==================== VALIDATION METHODS ====================

    private void validateUniqueDocument(String document) {
        if (clientPort.existsByDocument(document)) {
            throw new DuplicateIdentificationException(
                "Ya existe un cliente con identificación: " + document);
        }
    }

    private void validateUniqueEmail(String email) {
        if (clientPort.existsByEmail(email)) {
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
        int[] weights = {71, 67, 59, 53, 47, 43, 41, 37, 29};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nit.charAt(i)) * weights[i];
        }
        int remainder = sum % 11;
        int calculatedDv = (remainder > 1) ? (11 - remainder) : remainder;

        char dvChar = (calculatedDv == 10) ? '0' : Character.forDigit(calculatedDv, 10);
        return dvChar == Character.forDigit(expectedDv, 10);
    }
}
