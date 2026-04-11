# EVALUACION 2 - ConstruccionDeSoftware2-AldairMurilloMosquera

## Informacion general
- Estudiante(s): Integrantes no informados en README.md
- Rama evaluada: main
- Commit evaluado: cadcb830973b256753007a7dd5cbe22d0ddda847
- Fecha: 2026-04-11

---

## Tabla de calificacion

| Criterio | Peso | Puntaje (1-5) | Parcial |
|---|---|---|---|
| 1. Modelado de dominio | 20% | 3 | 0.60 |
| 2. Modelado de puertos | 20% | 3 | 0.60 |
| 3. Modelado de servicios de dominio | 20% | 3 | 0.60 |
| 4. Enums y estados | 10% | 4 | 0.40 |
| 5. Reglas de negocio criticas | 10% | 3 | 0.30 |
| 6. Bitacora y trazabilidad | 5% | 2 | 0.10 |
| 7. Estructura interna de dominio | 10% | 4 | 0.40 |
| 8. Calidad tecnica base en domain | 5% | 3 | 0.15 |
| **SUBTOTAL** | 100% | | **3.15** |

### Calculo
Nota base = Σ((puntaje_i / 5) * peso_i) / 20 = 63 / 20 = **3.15**

### Penalizaciones aplicadas
| Penalizacion | Motivo | Reduccion |
|---|---|---|
| Acoplamiento a framework | Anotacion @Service de Spring en clases de servicio de dominio (BankAccountDomainService, otros) | -25% |

Nota tras penalizacion: 3.15 × 0.75 = **2.36**

---

## Nota final
**2.4 / 5.0**

---

## Hallazgos

### Positivos
- **Estructura hexagonal presente:** Carpetas models/, ports/, services/ bien organizadas dentro del dominio.
- **Cuatro puertos con firmas semanticas:** BankAccountPort, ClientPort, LoanPort, TransferPort con metodos como `existsByAccountNumber()`, `findByDocument()`, `findByLoanStatus()`, `findByTransferStatus()`.
- **Cuatro servicios de dominio con logica:** BankAccountDomainService valida cliente existente y numero de cuenta unico; ClientDomainService y otros tienen validaciones similares.
- **Enums bien definidos:** AccountStatus, AccountType, Category, Currency, LoanStatus, LoanType, SystemRole, TransferStatus, UserStatus cubren los estados del enunciado.
- **Entidades completas en su mayoria:** BankAccount, Client, CorporateClient, Loan, Transfer, User, Person, GeneralBankProduct, OperationsLog, DetailData.
- **BusinessException** para manejo de reglas de negocio.

### Negativos
- **Acoplamiento Spring en dominio:** @Service y @RequiredArgsConstructor de Spring inyectados en BankAccountDomainService y otros servicios. Esto acopla el dominio al framework de infraestructura. Los servicios de dominio deben ser POJOs con constructor explicito.
- **ClientRole enum vacio:** `public enum ClientRole {}` no tiene ningun valor. La distincion entre cliente persona natural y empresa queda invalida.
- **Sin UserPort:** No existe puerto para la entidad Usuario. Los servicios de autenticacion y gestion de usuarios no tienen contrato de dominio.
- **Sin BitacoraPort:** OperationsLog existe como entidad pero no tiene puerto de salida. No se puede registrar eventos de auditoria desde el dominio.
- **OperationsLog por IDs:** Los campos `affectedProductId` (Long) y `userId` (Long) referencian por ID numerico en lugar de referencias a entidades de dominio. La trazabilidad queda incompleta.
- **Sin validacion de transferencias de alto monto:** No hay regla de aprobacion por supervisor ni vencimiento de 60 minutos en TransferDomainService.
- **TransferPort sin findPendingApprovalOlderThan:** No soporta la consulta de transferencias pendientes de aprobacion vencidas.
- **Client sin distincion de tipo:** La separacion entre PersonaNatural y Empresa se pierde (CorporateClient extiende Person directamente, sin pasar por Client).

---

## Recomendaciones
1. Eliminar @Service y @RequiredArgsConstructor de Spring de los servicios de dominio. Usar constructor explicito con los puertos como parametros.
2. Definir valores en ClientRole: NATURAL_PERSON, CORPORATE al menos.
3. Crear UserPort con: `findByUsername(String username)`, `existsByUsername(String)`, `save(User)`.
4. Crear BitacoraPort con: `void append(OperationsLog event)` y `List<OperationsLog> findByProductId(String productId)`.
5. Agregar `findPendingApprovalOlderThanMinutes(int minutes)` en TransferPort para soportar el vencimiento de transferencias de alto monto.
6. Agregar validacion de transferencia de alto monto en TransferDomainService: si monto > umbral y cuenta de empresa, requerir aprobacion de supervisor.
7. Revisar la jerarquia Cliente/CorporateClient y alinearla con el modelo del enunciado.
