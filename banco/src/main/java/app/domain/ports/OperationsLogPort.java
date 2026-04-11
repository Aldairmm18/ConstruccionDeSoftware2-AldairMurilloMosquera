package app.domain.ports;

import app.domain.models.OperationsLog;

public interface OperationsLogPort {
    OperationsLog save(OperationsLog log);
}
