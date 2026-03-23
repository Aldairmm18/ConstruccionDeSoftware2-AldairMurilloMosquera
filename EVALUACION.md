# EVALUACIÓN - ConstruccionDeSoftware2-AldairMurilloMosquera

## Información General
- **Estudiante:** Aldair Murillo Mosquera
- **Rama evaluada:** developed (única rama con código Java; `develop` no existe con ese nombre exacto)
- **Fecha de evaluación:** 2026-03-23

---

## Tabla de Calificación

| # | Criterio | Peso | Puntaje (1–5) | Nota ponderada |
|---|---|---|---|---|
| 1 | Modelado de dominio | 25% | 4 | 1.00 |
| 2 | Relaciones entre entidades | 15% | 4 | 0.60 |
| 3 | Uso de Enums | 15% | 4 | 0.60 |
| 4 | Manejo de estados | 5% | 5 | 0.25 |
| 5 | Tipos de datos | 5% | 3 | 0.15 |
| 6 | Separación Usuario vs Cliente | 10% | 5 | 0.50 |
| 7 | Bitácora | 5% | 3 | 0.15 |
| 8 | Reglas básicas de negocio | 5% | 4 | 0.20 |
| 9 | Estructura del proyecto | 10% | 5 | 0.50 |
| 10 | Repositorio | 10% | 3 | 0.30 |
| **TOTAL** | | **100%** | | **4.25 / 5 (base)** |

> Nota base = (4/5×0.25 + 4/5×0.15 + 4/5×0.15 + 5/5×0.05 + 3/5×0.05 + 5/5×0.10 + 3/5×0.05 + 4/5×0.05 + 5/5×0.10 + 3/5×0.10) × 5 = 0.85 × 5 = **4.25**

---

## Penalizaciones

Ninguna penalización aplicable.

---

## Bonus

| Bonus | % |
|---|---|
| Código limpio (arquitectura hexagonal bien aplicada, separación clara de capas) | +2% |
| Nombres claros y consistentes (inglés, camelCase, nombres semánticos) | +1% |

Nota con bonus: 4.25 × 1.03 = **4.38**

---

## Nota Final: **4.4 / 5.0**

---

## Análisis por Criterio

### 1. Modelado de dominio — 4/5
Entidades presentes: `Person` (base), `Client` (extiende `Person` → representa persona natural), `CorporateClient` (extiende `Person` → empresa), `User` (extiende `Person`), `BankAccount`, `Loan`, `Transfer`, `GeneralBankProduct`, `OperationsLog` (bitácora).  
**Observación de diseño:** `CorporateClient` extiende directamente `Person` en lugar de una clase `Client` base, lo que hace que ambos tipos de cliente sean hermanos sin una abstracción común. Para herencia correcta del dominio debería ser: `Client` (abstracta) → `PersonClient` y `CorporateClient`. Esto se penaliza con puntaje 4 en lugar de 5. La clase `ClientRole` existe pero está vacía.

### 2. Relaciones entre entidades — 4/5
`BankAccount` tiene referencia directa a `Client client` ✓.  
`Transfer` tiene `BankAccount sourceAccount` y `BankAccount targetAccount` ✓.  
`Loan` tiene `BankAccount disbursementTargetAccount` ✓ y `requestingClientId: Long` (ID en lugar de referencia a objeto).  
`OperationsLog` usa `Long userId` y `Long detailDataId` en vez de referencias a objetos — ligero acoplamiento por ID.

### 3. Uso de Enums — 4/5
Enums correctos: `AccountType`, `AccountStatus`, `Currency`, `LoanType`, `LoanStatus`, `TransferStatus`, `SystemRole`, `UserStatus`, `Category` ✓  
`ClientRole` existe pero está vacío — no tiene valores definidos.

### 4. Manejo de estados — 5/5
Todas las entidades son gestionadas con enums: `AccountStatus`, `LoanStatus`, `TransferStatus`, `UserStatus`. Los estados se asignan en los constructores y son tipados por enum.

### 5. Tipos de datos — 3/5
`Double` (wrapper) para montos monetarios — debería usarse `BigDecimal`. `LocalDate`/`LocalDateTime` correctamente utilizados para fechas ✓.

### 6. Separación Usuario vs Cliente — 5/5
`User extends Person` con credenciales, rol y estado del sistema. `Client extends Person` con información bancaria. Completamente separados. El `User` puede vincularse opcionalmente a un cliente a través del modelo. Separación impecable.

### 7. Bitácora — 3/5
`OperationsLog` registra la operación, tipo, fecha, producto afectado y referencia a `DetailData`. Sin embargo, `DetailData` es una clase con campos fijos (`entityType`, `previousValue`, `newValue`, `description`) — una estructura rígida, no flexible (no es `Map`, `JSON` ni campo genérico de detalles). Cumple parcialmente.

### 8. Reglas básicas de negocio — 4/5
Tiene servicios de dominio (`LoanDomainService`, `AccountManagementService`, `TransferDomainService`, `ClientDomainService`) y casos de uso (`AccountManagementUseCaseImpl`, `LoanManagementUseCaseImpl`, `TransferManagementUseCaseImpl`). La lógica de negocio está bien encapsulada en la capa de aplicación.

### 9. Estructura del proyecto — 5/5
Arquitectura hexagonal completa:
- `domain/models` — entidades y enums puro dominio
- `domain/ports` — interfaces de puertos (`BankAccountPort`, `ClientPort`, `LoanPort`, `TransferPort`)
- `domain/services` — servicios de dominio
- `application/usecases` — casos de uso con interfaces e implementaciones
- `application/adapters/persistence` — adaptadores de persistencia con entidades JPA
- `interfaces/controllers` — controladores REST con DTOs (requests)

Estructura profesional de primera clase.

### 10. Repositorio — 3/5
- **Nombre:** `ConstruccionDeSoftware2-AldairMurilloMosquera` ✓ formato correcto.
- **Commits:** 13 commits con mensajes descriptivos pero sin convención ADD/CHG.
- **README:** Contiene únicamente el título del repositorio — falta descripción, integrantes, tecnología y pasos de ejecución.
- **Ramas:** Usa `developed` en lugar de `develop` — nombre incorrecto según estándar Git Flow.
- **Tag de entrega:** No existe.

---

## Fortalezas
- Arquitectura hexagonal correctamente implementada (puertos, adaptadores, casos de uso, controladores).
- Código en inglés, limpio, con nombres semánticos consistentes.
- Enums completos para todos los catálogos del dominio.
- Servicios de dominio y casos de uso bien separados.
- Relaciones entre entidades mediante referencias a objetos.

## Oportunidades de mejora
- Corregir la jerarquía de clientes: crear `Client` (abstracta) → `PersonClient` y `CorporateClient` para que ambos tipos tengan una abstracción común.
- Completar el enum `ClientRole` con valores.
- Usar `BigDecimal` para montos monetarios.
- Cambiar la estructura de `DetailData` en `OperationsLog` a `Map<String, Object>` para mayor flexibilidad.
- Renombrar la rama `developed` a `develop`.
- Mejorar el README con información completa del proyecto.
- Agregar tag de entrega git.
- Adoptar convención de commits ADD/CHG.
