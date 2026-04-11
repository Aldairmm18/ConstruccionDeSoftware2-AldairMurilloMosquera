package app.domain.services;

import app.domain.models.PersonClient;
import app.domain.models.CorporateClient;
import app.domain.ports.ClientPort;
import app.domain.Exceptions.BusinessException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 * Domain service for client validation and business logic.
 */
@Service
@RequiredArgsConstructor
public class ClientDomainService {

    private final ClientPort clientPort;

    public void validateClientCreation(PersonClient client) {
        if (!ValidationUtils.isValidEmail(client.getEmail())) {
            throw new IllegalArgumentException("Invalid email format (RFC 5322 compliance required).");
        }
        
        if (clientPort.existsByDocument(client.getDocument())) {
            throw new BusinessException("A client with this document is already registered.");
        }

        if (clientPort.existsByEmail(client.getEmail())) {
            throw new BusinessException("A client with this email is already registered.");
        }
    }

    public void validateCorporateClient(CorporateClient client) {
        if (!ValidationUtils.isValidEmail(client.getEmail())) {
            throw new IllegalArgumentException("Invalid email format for corporate client.");
        }

        // NIT verification (NNNNNNNN-D format)
        if (client.getNit() != null && client.getNit().contains("-")) {
            String[] parts = client.getNit().split("-");
            if (parts.length == 2) {
                try {
                    int dv = Integer.parseInt(parts[1]);
                    if (!ValidationUtils.validateNitVerificationDigit(parts[0], dv)) {
                        throw new BusinessException("Invalid NIT verification digit (DV).");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid NIT format. Expected: NNNNNNNN-D");
                }
            }
        }
    }
}
