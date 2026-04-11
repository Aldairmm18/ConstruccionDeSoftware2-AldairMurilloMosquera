# Sistema de Gestión Bancaria Senior - Construcción de Software II

## 🏛️ Descripción General
Un sistema de gestión bancaria de alto rendimiento, seguro y arquitectónicamente blindado. Desarrollado con **Spring Boot 3.2.6** y **Java 17**, este proyecto sigue los principios de **Clean Architecture (Arquitectura Hexagonal)** y **Domain-Driven Design (DDD)** para garantizar la máxima mantenibilidad y seguridad.

El sistema ha sido sometido a un proceso de **Blindaje (Hardening)** profundo para asegurar la integridad de los datos y el cumplimiento de reglas de negocio críticas.

## 🛡️ Características de Blindaje y Reglas de Negocio

### 1. Seguridad de Acceso
- **BCrypt Password Hardening**: Todas las contraseñas se almacenan hasheadas con **BCrypt (12 rounds)**. Nunca se procesa texto plano.
- **Control de Roles**: Diferenciación estricta entre Administradores, Cajeros y Usuarios del sistema.

### 2. Integridad Financiera
- **Saldos No Negativos**: Validación forzada en el modelo de dominio (`BankAccount`). Es imposible realizar una operación que resulte en saldo negativo.
- **Validación de Identidad**: 
  - **NIT (Empresas)**: Validación algorítmica del dígito de verificación colombiano.
  - **Cédula (Personas)**: Validación de formato y longitud.
  - **Email/Teléfono**: Expresiones regulares estrictas (RFC 5322 y formato móvil colombiano).

### 3. Lógica de Operaciones (Business Rules)
- **Transferencias**:
  - Ventana de aprobación de **60 minutos**.
  - Tarea programada (`@Scheduled`) para expiración automática de transferencias pendientes.
  - Validación de cuenta origen != cuenta destino.
- **Préstamos (Loans)**:
  - Requisito de **mínimo 2 cuentas activas** para ser elegible.
  - Desembolso bloqueado si la cuenta destino no pertenece al solicitante.
  - Ciclo de vida: `UNDER_REVIEW` ➔ `APPROVED` ➔ `DISBURSED`.

### 4. Auditabilidad (Operations Log)
- **Ledger Inmutable**: Cada depósito, retiro, transferencia y préstamo genera un registro en el log de operaciones (`OperationsLog`) con detalles técnicos y marcas de tiempo.

## 🚀 Stack Tecnológico
- **Lenguaje**: Java 17 (Requerido)
- **Framework**: Spring Boot 3.2.6
- **Persistencia**: JPA / Hibernate con MySQL 8.0
- **Seguridad**: Spring Security 6 + BCrypt
- **Nomenclatura**: Código en **Inglés profesional**, Mensajes de error en **Español** para el usuario.

## 🏗️ Estructura del Proyecto
El proyecto sigue el patrón de **Arquitectura Hexagonal**:

```text
src/main/java/app/
├── domain/                    # Lógica de Negocio (Core)
│   ├── models/                # Entidades de Dominio (BankAccount, Loan, User)
│   ├── ports/                 # Interfaces (Contratos de infraestructura)
│   ├── services/              # SERVICIOS CONSOLIDADOS (TransactionService, TransferService)
│   └── Exceptions/            # Excepciones personalizadas (InsuficienteFundsException)
├── application/               # Adaptadores de Entrada/Salida
│   └── adapters/
│       └── persistence/       # Implementación SQL (Spring Data JPA)
├── infrastructure/            # Aspectos Cross-cutting (Security, Filter, Config)
└── interfaces/                # Adaptadores de Entrada REST (Controllers)
```

## ⚙️ Cómo Ejecutar el Proyecto

### 1. Requisitos
- **JDK 17** y **MySQL Server**.

### 2. Configuración
Ajustar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banco?createDatabaseIfNotExist=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 3. Ejecución
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"; .\mvnw spring-boot:run
```

### 4. Pruebas (Postman)
Se incluye la colección **`Hardened_Banking_System.postman_collection.json`** en la raíz del repositorio con todos los endpoints actualizados y listos para probar.

## 👥 Autores
- **Desarrollador**: Aldair Murillo Mosquera - Tecnológico de Antioquia
- **Consultor Especialista**: Antigravity AI (Implementation & Hardening)
- **Profesor**: Andrés Felipe Sánchez

---
*Este sistema ha sido blindado para prevenir bypass de seguridad y asegurar un registro contable de grado bancario.*
