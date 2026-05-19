# Cambios Realizados — Correcciones al Proyecto Bancario

> Rama: `develop` | Fecha: 2026-05-19

---

## 1. Secreto JWT externalizado

**Problema:** La clave secreta del token JWT estaba hardcodeada directamente en el código fuente, lo cual la expone a cualquier persona con acceso al repositorio y es una vulnerabilidad crítica de seguridad.

**Archivos modificados:**
- `banco/src/main/resources/application.properties`
- `banco/src/main/java/app/infrastructure/security/JwtUtil.java`

**Qué se hizo:**
- Se movió el secreto a `application.properties` bajo la clave `jwt.secret`.
- En `JwtUtil.java` se reemplazó el campo `private final String SECRET = "..."` por una inyección de Spring:
  ```java
  @Value("${jwt.secret}")
  private String secret;
  ```

---

## 2. Eliminación de comparación de contraseña en texto plano

**Problema:** El modelo `User.java` tenía un método `verifyPassword(String enteredPassword)` que comparaba contraseñas con `.equals()`, sin pasar por BCrypt. Si alguien llamaba este método en lugar del servicio, estaría comparando la contraseña hasheada contra texto plano, con resultado siempre falso o en peor caso exponiendo lógica insegura.

**Archivo modificado:**
- `banco/src/main/java/app/domain/models/User.java`

**Qué se hizo:**
- Se eliminó completamente el método `verifyPassword()`. La autenticación siempre debe delegarse al `UserService` que usa `passwordHasher.matches()` de BCrypt.

---

## 3. Restricción del CORS

**Problema:** La configuración de CORS permitía peticiones desde cualquier origen (`*`), lo cual en producción abre la API a cualquier dominio.

**Archivo modificado:**
- `banco/src/main/java/app/infrastructure/security/SecurityConfig.java`

**Qué se hizo:**
- Se reemplazó `setAllowedOrigins(List.of("*"))` por `setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"))`, restringiendo el acceso solo a orígenes locales durante el desarrollo.

---

## 4. Delegación de lógica a servicios de dominio (resolución de TODOs)

**Problema:** Los cuatro casos de uso (`ClientManagementUseCaseImpl`, `AccountManagementUseCaseImpl`, `TransactionManagementUseCaseImpl`, `LoanManagementUseCaseImpl`, `TransferManagementUseCaseImpl`) tenían el mismo comentario `// TODO: Delegate business logic to domain services instead of implementing directly here`. La lógica de negocio estaba duplicada: existía tanto en los casos de uso como en los servicios de dominio sin que nadie la llamara.

**Estrategia aplicada:**
1. Se creó `DomainServicesConfig.java` para registrar todos los servicios de dominio como beans de Spring (ya que son clases Java puras sin `@Service`).
2. Se refactorizaron los casos de uso para inyectar y delegar a los servicios de dominio correspondientes.
3. Se alinearon los estados de préstamo y transferencia entre la capa de dominio y la de aplicación.

### 4.1 — Nuevo archivo: `DomainServicesConfig.java`

**Archivo creado:**
- `banco/src/main/java/app/infrastructure/config/DomainServicesConfig.java`

Clase `@Configuration` que instancia y expone como beans todos los servicios de dominio:
`ValidationService`, `AuditService`, `ClientService`, `AccountService`, `DepositService`, `WithdrawalService`, `BillPaymentService`, `TransactionQueryService`, `LoanService`, `TransferService`.

### 4.2 — `ClientManagementUseCaseImpl`

**Antes:** Duplicaba validaciones de documento/email, llamaba directamente a `clientPort` y `operationsLogPort`.

**Después:** Delega completamente a `ClientService`:
- `registerNaturalPerson` → `clientService.saveNaturalPerson()`
- `registerCorporateCompany` → `clientService.saveCorporateCompany()`
- `updateContactInfo` → `clientService.updateContactInfo()`
- `findByIdentification` → `clientService.findByDocument()`
- `findAll` → `clientService.findAll()`

Se eliminaron las dependencias a `ClientPort` y `OperationsLogPort` del caso de uso.

### 4.3 — `AccountManagementUseCaseImpl`

**Antes:** Duplicaba generación de número de cuenta, validación de depósito inicial, llamadas directas a `bankAccountPort` y `operationsLogPort`.

**Después:** Delega completamente a `AccountService`:
- `openSavingsAccount` → `accountService.openSavingsAccount()`
- `openCheckingAccount` → `accountService.openCheckingAccount()`
- `changeAccountType` → `accountService.changeAccountType()`
- `getBalance` → `accountService.getBalance()`
- `findAll/findById/findByClientId` → métodos equivalentes en `AccountService`

Se eliminaron dependencias a `ClientPort`, `OperationsLogPort` y la lógica de `generateUniqueAccountNumber()`.

### 4.4 — `TransactionManagementUseCaseImpl`

**Antes:** Duplicaba lógica de depósito, retiro y pago de servicios inline.

**Después:** Delega a servicios especializados de dominio:
- `makeDeposit` → `DepositService.executeDeposit()`
- `makeWithdrawal` → `WithdrawalService.executeWithdrawal()`
- `payService` → `BillPaymentService.payBill()`
- `getTransactionsByAccount` → `TransactionQueryService.findByAccountNumber()`

### 4.5 — `LoanManagementUseCaseImpl`

**Antes:** Duplicaba validaciones de cliente, cuentas activas, estado del préstamo.

**Después:** La validación de **rol** (`INTERNAL_ANALYST`) permanece en el caso de uso (es orquestación), y el resto delega a `LoanService`:
- `requestLoan` → `loanService.requestLoan()`
- `approveLoan` → validación de rol + `loanService.approveLoan()`
- `rejectLoan` → validación de rol + `loanService.rejectLoan()`
- `disburseLoan` → `loanService.disburseLoan()`

Se extrajo `requireRole()` como método privado reutilizable.

### 4.6 — `TransferManagementUseCaseImpl`

**Antes:** Duplicaba validaciones de cuentas, saldo, lógica de alto valor.

**Después:** La validación de **rol** (`CORPORATE_SUPERVISOR` / `INTERNAL_ANALYST`) permanece en el caso de uso, el resto delega a `TransferService`:
- `requestTransfer` → `transferService.requestTransfer()`
- `approveTransfer` → validación de rol + `transferService.approveTransfer()`
- `rejectTransfer` → validación de rol + `transferService.rejectTransfer()`
- `expirePendingTransfers` (`@Scheduled`) → `transferService.expirePendingTransfers()`

Se extrajo `requireSupervisorOrAnalyst()` como método privado reutilizable.

### 4.7 — Alineación de estados en servicios de dominio

**`LoanService.java`:**
- `requestLoan`: estado inicial cambiado de `LoanStatus.PENDING` → `LoanStatus.UNDER_REVIEW`
- `validatePendingStatus`: condición cambiada de `PENDING` → `UNDER_REVIEW`

**`TransferService.java`:**
- `approveTransfer`: estado final cambiado de `TransferStatus.APPROVED` → `TransferStatus.EXECUTED`

Estos cambios alinean los servicios de dominio con el comportamiento real del sistema que estaba en los casos de uso.

### 4.8 — Nuevos métodos en `ClientService`

Se agregaron dos métodos para aceptar objetos ya construidos (provenientes de la capa de controladores):

```java
public PersonClient saveNaturalPerson(PersonClient client)
public CorporateClient saveCorporateCompany(CorporateClient company)
```

Cada uno valida unicidad de documento y email, establece el estado `ACTIVE`, persiste y registra en auditoría.

---

## 5. Documentación de API con Swagger / OpenAPI

**Problema:** No había forma de explorar la API de manera interactiva.

**Archivos modificados:**
- `banco/pom.xml`
- `banco/src/main/resources/application.properties`

**Qué se hizo:**
- Se agregó la dependencia `springdoc-openapi-starter-webmvc-ui` versión `2.6.0` al `pom.xml`.
- Se configuró en `application.properties`:
  - `springdoc.api-docs.path=/api-docs`
  - `springdoc.swagger-ui.path=/swagger-ui.html`

Con esto, al levantar el proyecto, la documentación interactiva estará disponible en: `http://localhost:8080/swagger-ui.html`

---

## Resumen de archivos

| Archivo | Acción |
|---|---|
| `application.properties` | Modificado — jwt.secret y springdoc config |
| `JwtUtil.java` | Modificado — @Value en lugar de hardcode |
| `User.java` | Modificado — eliminado verifyPassword() |
| `SecurityConfig.java` | Modificado — CORS restringido a localhost |
| `pom.xml` | Modificado — dependencia springdoc agregada |
| `DomainServicesConfig.java` | **Creado** — beans de servicios de dominio |
| `ClientService.java` | Modificado — métodos saveNaturalPerson/saveCorporateCompany |
| `LoanService.java` | Modificado — estado UNDER_REVIEW alineado |
| `TransferService.java` | Modificado — estado EXECUTED alineado |
| `ClientManagementUseCaseImpl.java` | Modificado — delega a ClientService |
| `AccountManagementUseCaseImpl.java` | Modificado — delega a AccountService |
| `TransactionManagementUseCaseImpl.java` | Modificado — delega a servicios especializados |
| `LoanManagementUseCaseImpl.java` | Modificado — delega a LoanService |
| `TransferManagementUseCaseImpl.java` | Modificado — delega a TransferService |
