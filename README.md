# Sistema de Gestión Bancaria - Construcción de Software II

## Descripción
Aplicación de gestión de información de un banco desarrollada con Spring Boot siguiendo arquitectura DDD (Domain-Driven Design) con patrón hexagonal.

## Tecnologías
- Java 17
- Spring Boot 3.2.6
- MySQL
- Lombok
- Maven

## Arquitectura
```
src/main/java/app/
+-- domain/
¦   +-- models/          # Entidades del dominio
¦   +-- ports/           # Interfaces (puertos)
¦   +-- services/        # Lógica de negocio
¦   +-- Exceptions/      # Excepciones de negocio
+-- application/
¦   +-- usecases/        # Casos de uso
+-- interfaces/
    +-- controllers/     # API REST
```

## Modelo de Dominio
- **Person**: Clase padre con datos comunes
- **PersonClient**: Cliente persona natural (hereda de Person)
- **CorporateClient**: Cliente empresa (hereda de Person)
- **User**: Usuario del sistema (empleados del banco)
- **BankAccount**: Cuentas bancarias
- **Loan**: Préstamos/Créditos
- **Transfer**: Transferencias entre cuentas
- **GeneralBankProduct**: Catálogo de productos
- **OperationsLog**: Bitácora de auditoría (NoSQL)

## Autor
Aldair Murillo Mosquera - Tecnológico de Antioquia

## Profesor
Andrés Felipe Sánchez
