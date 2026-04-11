package app.domain.services;

import app.domain.models.PersonClient;
import app.domain.models.CorporateClient;
import app.domain.ports.ClientPort;
import app.domain.Exceptions.BusinessException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientDomainService {

    private final ClientPort clientPort;

    public void validateClientCreation(PersonClient client) {
        if (!ValidationUtils.isValidEmail(client.getEmail())) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
        if (clientPort.existsByDocument(client.getDocument())) {
            throw new BusinessException("El documento ya se encuentra registrado.");
        }
        if (clientPort.existsByEmail(client.getEmail())) {
            throw new BusinessException("El correo ya se encuentra registrado.");
        }
    }

    public void validateCorporateClient(CorporateClient client) {
        if (!ValidationUtils.isValidEmail(client.getEmail())) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
        // Assuming NIT format might be 12345678-9
        if (client.getNit() != null && client.getNit().contains("-")) {
            String[] parts = client.getNit().split("-");
            if (parts.length == 2) {
                try {
                    int dv = Integer.parseInt(parts[1]);
                    if (!ValidationUtils.validateNIT(parts[0], dv)) {
                        throw new BusinessException("El dígito de verificación del NIT es incorrecto.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de NIT inválido. Debe ser NNNNNNNN-D");
                }
            }
        }
    }
}
