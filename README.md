# 🍽️ Ambigu - Sistema de Gestión de Restaurantes

Ambigu es una aplicación backend desarrollada con Spring Boot 3.4.2 y MongoDB, diseñada para gestionar operaciones esenciales en un restaurante. Este backend proporciona una API REST robusta para el manejo de usuarios, autenticación con JWT, pedidos, planes de trabajo, administración de roles y más.

⚠️ Este repositorio corresponde exclusivamente a la parte backend del sistema. El frontend se desarrollará y documentará por separado.
---

## 🚀 Tecnologías Utilizadas

- ⚙️ Java 21
- ☕ Spring Boot 3.4.2
  - `spring-boot-starter-web`
  - `spring-boot-starter-security`
  - `spring-boot-starter-data-mongodb`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-actuator`
  - `spring-boot-starter-mail`
  - `spring-boot-starter-thymeleaf`
- 💾 MongoDB (Atlas)
- 🔐 JWT (Json Web Token)
- 📖 Swagger / OpenAPI (`springdoc-openapi`)
- 🛠️ Lombok
- ✉️ Email por SMTP con Gmail
- 🌐 Deploy Frontend en S3 (AWS)

---

## 🗂️ Estructura del Proyecto

Este backend sigue una estructura organizada por objetos de negocio (domain-driven structure). Cada módulo relacionado (por ejemplo: `auth`, `order`, `workplan`) contiene su propio conjunto de archivos.

## ⚙️ Configuración

La configuración del sistema se encuentra definida en el archivo `src/main/resources/application.properties`. Ahí se establecen los parámetros de conexión a MongoDB, SMTP, URLs externas y más.

## 🛠️ Requisitos
Java 21
Maven 3.8+
MongoDB (local o Atlas)
Cuenta de Gmail (para SMTP)

## ▶️ Cómo ejecutar
# Clona el repositorio
git clone https://github.com/Flawless-Coders/ambigu-backend
cd ambigu

# Construye el proyecto y ejecutar el proyecto
mvn clean install
mvn spring-boot:run
# opción 2, ejecuta la clase principal -> AmbiguApplication (directorio src/main/java)

## 🔐 Seguridad
El backend utiliza JWT (Json Web Token) para autenticación y autorización. Los tokens se generan al hacer login y deben incluirse en los encabezados de las peticiones protegidas.

## 📚 Documentación API
Una vez corriendo, puedes acceder a la documentación Swagger en:
http://localhost:8080/swagger-ui.html

## 🧑‍💻 Desarrollado por
Flawless Coders:
    - Hernández Sánchez Katia Alexandra
    - Higareda Vázquez María del Pilar
    - Joaquín Landa Martín Antonio
    - León Flores Axel Daniel
    - Miranda Roldán Jose Luis 