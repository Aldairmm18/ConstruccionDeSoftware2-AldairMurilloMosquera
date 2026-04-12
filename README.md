# Sistema de Gestión Bancaria - Construcción de Software II

## Descripción General

Sistema bancario desarrollado con **Spring Boot 3.3.6** y **Java 21**, aplicando los principios de **Arquitectura Hexagonal (Clean Architecture)** y **Domain-Driven Design (DDD)**.

El dominio está completamente aislado de Spring y JPA. Las capas de aplicación, interfaces e infraestructura son las únicas que dependen de frameworks externos.

## Información Académica

- **Estudiante**: Aldair Murillo Mosquera
- **Profesor**: Andrés Felipe Sánchez
- **Materia**: Construcción de Software II
- **Institución**: Tecnológico de Antioquia

## Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.3.6 | Framework de aplicación |
| Spring Security | 6 | Autenticación JWT + BCrypt |
| Spring Data JPA | 3.3.6 | Persistencia (solo en capa application) |
| MySQL | 8.0 | Base de datos relacional |
| Lombok | latest | Reducción de boilerplate |
| JJWT | 0.12+ | Generación y validación de tokens JWT |
| Maven | 3.x | Gestión de dependencias |

## Arquitectura - Regla Clave

```
domain/     → Java PURO. CERO Spring. CERO JPA.
application/ → Spring (@Service, @Transactional, JPA Entities, Adapters)
interfaces/ → Spring (@RestController, DTOs)
infrastructure/ → Spring (@Component, Security, JWT)
```

## Estructura de Carpetas

```
banco/src/main/java/app/
├── domain/
│   ├── models/          # Modelos de dominio puros (sin @Entity)
│   │   ├── BankAccount.java, Client.java, PersonClient.java
│   │   ├── CorporateClient.java, User.java, Loan.java
│   │   ├── Transfer.java, Transaction.java, OperationsLog.java
│   │   └── enums: AccountType, AccountStatus, ClientStatus,
│   │             Currency, LoanStatus, LoanType, TransferStatus,
│   │             TransactionType, SystemRole, UserStatus, ClientRole
│   ├── ports/           # Interfaces puras (contratos hexagonales)
│   │   ├── BankAccountPort, ClientPort, LoanPort
│   │   ├── TransferPort, TransactionPort, UserPort
│   │   ├── OperationsLogPort, PasswordHasher
│   └── services/        # Logica de negocio pura (sin @Service)
│       ├── AccountService, ClientService, LoanService
│       ├── TransferService, DepositService, WithdrawalService
│       ├── BillPaymentService, UserService, AuditService
│       ├── TransactionQueryService, ValidationService
│   └── Exceptions/      # Excepciones de negocio
├── application/
│   ├── usecases/        # Casos de uso (@Service, @Transactional)
│   │   ├── AccountManagementUseCase(Impl)
│   │   ├── ClientManagementUseCase(Impl)
│   │   ├── TransferManagementUseCase(Impl)
│   │   ├── TransactionManagementUseCase(Impl)
│   │   └── LoanManagementUseCase(Impl)
│   └── adapters/persistence/sql/
│       ├── entities/    # JPA Entities (@Entity)
│       ├── repositories/ # Spring Data JPA repositories
│       └── *PersistenceAdapter.java  # Implementan los ports
├── interfaces/
│   └── controllers/     # REST Controllers (@RestController)
│       ├── AuthController, BankAccountController
│       ├── ClientController, TransferController
│       ├── TransactionController, LoanController
│       └── requests/, responses/  # DTOs
└── infrastructure/
    └── security/        # JWT + Spring Security
        ├── SecurityConfig, JwtUtil
        ├── JwtAuthenticationFilter
        ├── UserDetailsServiceImpl
        └── BCryptPasswordHasherAdapter
```

## Reglas de Negocio

- **Saldo no negativo**: validado en `BankAccount.debit()` (dominio)
- **Transferencias**: expiran en 60 minutos; auto-expiradas por `@Scheduled`
- **Transferencias de alto monto**: >10M COP desde cuenta empresarial → estado `AWAITING_APPROVAL`
- **Prestamos**: requieren minimo 2 cuentas activas para aplicar
- **Autenticacion**: maximo 5 intentos fallidos → bloqueo 15 minutos
- **Contrasenas**: BCrypt 12 rounds (solo en capa infrastructure)
- **NIT empresas**: validacion algoritmica del digito verificador colombiano
- **Cedula personas**: validacion de formato 7-10 digitos
- **Email/Telefono**: regex estricto (formato colombiano movil 3XXXXXXXXX)

## Como Ejecutar

### Requisitos
- JDK 21
- MySQL 8.0

### Configuracion
Editar `banco/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banco?createDatabaseIfNotExist=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contrasena
```

### Ejecucion
```bash
cd banco
./mvnw spring-boot:run
```

### Pruebas
Se incluye `Hardened_Banking_System.postman_collection.json` en la raiz del repositorio con todos los endpoints.

---

*Proyecto academico - Construccion de Software II - Tecnologico de Antioquia*
