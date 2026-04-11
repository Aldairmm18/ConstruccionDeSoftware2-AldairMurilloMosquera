# Sistema de Gestión Bancaria Senior - Construcción de Software II

## 🏛️ Descripción General
Un sistema de gestión bancaria de alto rendimiento, seguro y arquitectónicamente blindado. Desarrollado con **Spring Boot 3.2.6** y **Java 17**, este proyecto sigue los principios de **Clean Architecture (Arquitectura Hexagonal)** y **Domain-Driven Design (DDD)** para garantizar la máxima mantenibilidad y seguridad.

### 🛡️ Características Clave y Blindaje (Hardening)
- **Consolidated Business Services**: Lógica agregada para operaciones core (Transactions, Clients, Loans) evitando el antipatrón de servicios CRUD simples.
- **Automated Ledger (Audit Trail)**: Cada operación (Deposit, Withdrawal, Transfer) se registra automáticamente en un libro mayor inmutable (tabla `Transfer`) para asegurar la auditabilidad total.
- **Automated Loan Lifecycle**: Gestión completa de préstamos desde la solicitud hasta el desembolso automático garantizado por el sistema tras la aprobación.
- **Guardias de Seguridad**:
  - **BCrypt Password Hardening**: Codificación forzada para la seguridad de todos los usuarios.
  - **Account Integrity**: Eliminación de entrada manual para números de cuenta mediante generación automática.
  - **Strict Naming**: Nomenclatura unificada en inglés para contratos de API y modelos de dominio.
  - **Email Compliance**: Validación mediante regex RFC 5322 para todas las entidades del sistema.

## 🚀 Stack Tecnológico
- **Lenguaje**: Java 17 (Requerido)
- **Framework**: Spring Boot 3.2.6
- **Persistencia**: JPA / Hibernate con soporte para MySQL 8.0
- **Utilidades**: Lombok 1.18+ (Generación de bytecode), Maven 3.9+
- **Seguridad**: Spring Security 6 con capa de JWT

## 🏗️ Diseño de Arquitectura
El proyecto está estrictamente organizado usando el patrón Hexagonal para desacoplar la lógica de negocio de la infraestructura:

```text
src/main/java/app/
├── domain/                    # El Corazón: Lógica de Negocio Pura
│   ├── models/                # Objetos de Dominio (BankAccount, Loan, User)
│   ├── ports/                 # Interfaces (Contratos para Persistencia/API)
│   ├── services/              # CONSOLIDATED SERVICES (TransactionService, LoanService)
│   └── Exceptions/            # Errores específicos de negocio (InsufficientFunds)
├── application/               # Capa de Adaptadores
│   └── adapters/
│       └── persistence/       # Implementación SQL (PersistenceAdapters)
└── infrastructure/            # Aspectos transversales (Security, Config)
```

## ⚙️ Cómo Ejecutar el Proyecto
### 1. Requisitos
- **JDK 17**: Asegúrate de que `JAVA_HOME` apunte a tu instalación de JDK 17.
- **Servidor MySQL**: Corriendo en el puerto 3306.

### 2. Configuración
Actualiza el archivo `src/main/resources/application.properties` con tus credenciales de base de datos:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banco?createDatabaseIfNotExist=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 3. Ejecución
Ejecuta mediante el Maven Wrapper:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"; .\mvnw spring-boot:run
```

## 👥 Autores
- **Desarrollador**: Aldair Murillo Mosquera - Tecnológico de Antioquia
- **Especialista Consultor**: Antigravity AI (Implementación y Blindaje)
- **Profesor**: Andrés Felipe Sánchez

---
*Este sistema ha sido blindado arquitectónicamente para prevenir el bypass de reglas de negocio y asegurar un libro mayor (ledger) de grado profesional.*
