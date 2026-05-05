# Sistema de Gestión Bancaria

## Construcción de Software II - Tecnológico de Antioquia

### Estudiante
Aldair Murillo Mosquera

### Profesor
Andrés Felipe Sánchez

### Descripción
Sistema de gestión bancaria desarrollado con Spring Boot siguiendo arquitectura DDD (Domain-Driven Design) con patrón hexagonal. Permite gestionar clientes, cuentas bancarias, préstamos, transferencias y auditoría de operaciones.

### Tecnologías
- Java 21
- Spring Boot 3.3.6
- MySQL (datos relacionales)
- MongoDB (bitácora de operaciones)
- Spring Security + JWT
- Lombok
- Maven

### Arquitectura
```
src/main/java/app/
├── domain/                 # Dominio puro (sin Spring, sin JPA)
│   ├── models/             # Entidades del dominio
│   ├── ports/              # Interfaces (puertos)
│   ├── services/           # Lógica de negocio
│   └── Exceptions/         # Excepciones de negocio
├── application/
│   ├── usecases/           # Casos de uso
│   └── adapters/
│       └── persistence/
│           ├── sql/        # MySQL (JPA)
│           └── nosql/      # MongoDB (Bitácora)
├── interfaces/
│   └── controllers/        # API REST + DTOs
└── infrastructure/
    └── security/           # JWT + Spring Security
```

### Modelo de Dominio
- **Person** → Client → PersonClient / CorporateClient
- **Person** → User (empleados del banco)
- **BankAccount**, **Loan**, **Transfer**, **Transaction**
- **OperationsLog** (MongoDB - auditoría)
- **GeneralBankProduct** (catálogo)

### Endpoints principales
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/users/register | Registrar usuario |
| POST | /auth/login | Obtener token JWT |
| POST | /api/clients/natural | Crear cliente persona natural |
| POST | /api/clients/corporate | Crear cliente empresa |
| POST | /api/accounts/savings | Abrir cuenta de ahorros |
| POST | /api/transfers/request | Crear transferencia |
| POST | /api/transfers/{id}/approve | Aprobar transferencia |
| POST | /api/transfers/{id}/reject | Rechazar transferencia |
| POST | /api/loans/request | Solicitar préstamo |
| POST | /api/loans/{id}/approve | Aprobar préstamo |
| POST | /api/loans/{id}/reject | Rechazar préstamo |
| POST | /api/loans/{id}/disburse | Desembolsar préstamo |

### Reglas de Negocio
- Solo INTERNAL_ANALYST puede aprobar/rechazar préstamos
- Solo CORPORATE_SUPERVISOR o INTERNAL_ANALYST puede aprobar/rechazar transferencias
- Transferencias de empresa > 10M COP requieren aprobación
- Transferencias pendientes se vencen a los 60 minutos
- Contraseñas encriptadas con BCrypt
- Autenticación via JWT (10 horas de validez)

### Ejecución
```bash
cd banco
./mvnw spring-boot:run
```

### Tests
```bash
cd banco
./mvnw test
```
