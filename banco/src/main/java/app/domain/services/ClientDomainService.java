package app.domain.services;

import app.domain.models.PersonClient;
import app.domain.ports.ClientPort;
import app.domain.Exceptions.BusinessException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientDomainService {

    private final ClientPort clientPort;

    public void validateClientCreation(PersonClient client) {
        if (clientPort.existsByDocument(client.getDocument())) {
            throw new BusinessException("El documento ya se encuentra registrado.");
        }
        if (clientPort.existsByEmail(client.getEmail())) {
            throw new BusinessException("El correo ya se encuentra registrado.");
        }
    }
}
