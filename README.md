# API de Juegos de preguntas soportados con IA

## Descripción

Este proyecto es una API desarrollada con Spring Boot 3.2.2 para jugar a juegos de preguntas creados y soportados por IA mediante la API de OpenAI.


## Tabla de Contenidos

- [Descripción](#descripción)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Pruebas](#pruebas)
- [Contacto](#contacto)

## Requisitos

- Java 17
- Maven 4.0.0
- Docker
- Tener Instalado JDK 17 y  configurado JAVA_HOME si no se tiene un IDE que pueda ejecutar Java con un JDK propio dentro del IDE
- Recomendable utilizar IDEs como IntelliJ IDEA (tanto la versión de pago Ultimate como la gratuita Community Edition), ya que tiene una mejor integración con Java y Maven

## Instalación

1. Clona el repositorio:

   ```bash
   git clone https://github.com/angelvido/games-backend.git
   cd games-backend

2. Compila el proyecto usando Maven

   ```bash
   %Si se tiene maven instalado
   mvn clean install
   %Si se utiliza un wrapper de maven en Windows
   .\mvnw.cmd clean install
   %Si se utiliza un wrapper de maven en MAC/UNIX
   ./mvnw clean install

3. Levanta la imagen de la base de datos con Docker:

   ```bash
   cd docker
   docker compose up

## Configuración

Configura la propiedad de la API Key con tu API Key de la API de OpenAI en `src/main/resources/application.properties`:

    # OpenAI API Configuration
    openai.model.gpt35=gpt-3.5-turbo
    openai.model.gpt4=gpt-4-turbo
    openai.model.embedding=text-embedding-3-small
    openai.api.chat.url=https://api.openai.com/v1/chat/completions
    openai.api.embedding.url=https://api.openai.com/v1/embeddings
    openai.api.key="TU-API-KEY"

## Ejecucion

Para ejecutar la aplicación localmente:

    %Si se tiene maven instalado
    mvn spring-boot:run
    %Si se utiliza un wrapper de maven en Windows
    .\mvnw.cmd spring-boot:run
    %Si se utiliza un wrapper de maven en MAC/UNIX
    ./mvnw spring-boot:run

## Contacto

Ángel Vidal Domínguez - vidaldominguezangel@gmail.com

Proyecto Link: https://github.com/angelvido/games-backend