# LangChain4j Demo - Asistente IA Inteligente con Spring Boot y RAG

Aplicación backend desarrollada en **Java 21** y **Spring Boot 3.3.3** que integra **LangChain4j**, un modelo de lenguaje de gran escala (LLM) a través de **Groq**, **RAG (Retrieval-Augmented Generation)** con documentos locales, **herramientas personalizadas (Tools)** y **memoria conversacional persistente** respaldada por una base de datos relacional.

---

## 🚀 Tecnologías y Stack Utilizado

* **Java 21**
* **Spring Boot 3.3.3** (Web, Data JPA)
* **LangChain4j (v0.35.0)** (Framework de integración de IA para Java)
* **Groq API** (Proveedor LLM compatible con OpenAI - Modelo: `openai/gpt-oss-20b`)
* **PostgreSQL** (Persistencia del historial de chat)
* **Maven** (Gestión de dependencias)

---

## 📂 Arquitectura del Proyecto

```text
src/
├── main/
│   ├── java/com/cris959/langchain4j_demo/
│   │   ├── config/          # Configuración de beans, RAG, modelo de embeddings y memoria DB
│   │   ├── controller/      # Endpoints REST para interactuar con el asistente
│   │   ├── dto/             # Objetos de transferencia de datos desacoplados (ChatRequest)
│   │   ├── entity/          # Entidades JPA para la persistencia de mensajes
│   │   ├── repository/      # Repositorios Spring Data JPA
│   │   ├── service/         # Interfaces AI Service y lógica de negocio
│   │   └── tools/           # Herramientas personalizadas (@Tool)
│   └── resources/
│       ├── documents/       # Archivos de texto/políticas para RAG
│       └── application.properties
```

# ✨ Características Principales
1- Chat Conversacional Inteligente:

° Conectado al ecosistema de Groq mediante la API compatible con OpenAI para respuestas rápidas y fluidas.

2- Memoria de Conversación Persistente (PostgreSQL):

°Implementación de la interfaz **ChatMemoryStore** conectada a Spring Data JPA. El historial de chat por usuario (**memoryId**) se almacena de forma segura en una base de datos relacional y sobrevive a reinicios del servidor.

3- RAG Local (Búsqueda Documental):

° Carga automática de documentos de negocio (como políticas de evaluación) desde la carpeta **src/main/resources/documents/** usando el **ResourceLoader** de Spring.

° Vectorización mediante un modelo local ligero (**AllMiniLmL6V2EmbeddingModel**) y almacenamiento en memoria para consultas semánticas ultrarrápidas.

4- Herramientas Personalizadas (**@Tool**):

° El asistente es capaz de invocar de manera autónoma funciones programadas en Java (cálculo de descuentos, hora del servidor, etc.) cuando el usuario lo requiere.

# ⚙️ Configuración y Variables de Entorno
Para ejecutar la aplicación en tu entorno local, debes configurar las siguientes variables de entorno en tu IDE o sistema operativo:

1. **Obtén tu API Key de Groq:** Puedes generar una clave de acceso gratuita ingresando a la [Consola de Groq (Groq Cloud)](https://console.groq.com/).

| Variable | Descripción | Ejemplo / Valor |
|----------|-------------|-----------------|
| `GROQ_API_KEY` | Clave de acceso a la API de Groq | `gsk_...` |
| `GROQ_BASE_URL` | URL base del proveedor | `https://api.groq.com/openai/v1` |
| `GROQ_MODEL_NAME` | Nombre del modelo LLM activo | `openai/gpt-oss-20b` |
| `DB_URL` | Conexión JDBC a PostgreSQL | `jdbc:postgresql://localhost:5432/langchain4j_demo` |
| `DB_USER` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `tu_contraseña` |

# 🔌 Endpoints Principales
Enviar mensaje al Asistente
URL: **/api/chat**

Método: **POST** o **GET**

Parámetros / Request Body:

````json
{
  "usuarioId": "user123",
  "mensaje": "¿Cuál es la puntuación para aprobación del alumno?"
}
````
° Respuesta: Texto generado por la IA combinando el historial de chat, la memoria persistente y el contexto recuperado de los documentos locales (RAG). 

# 🛠️ Cómo Ejecutar el Proyecto
1- Clonar el repositorio.

2- Configurar una base de datos local en PostgreSQL llamada **langchain4j_demo**.

3- Añadir tus archivos de contexto (ej. políticas, manuales) en **src/main/resources/documents/**.

4- Definir las variables de entorno de Groq y de la base de datos.

5- Ejecutar la clase principal **Langchain4jDemoApplication** desde tu IDE (IntelliJ IDEA, Eclipse) o mediante Maven:

````bash
mvn spring-boot:run
````

# LangChain4j Demo - Asistente RAG con Spring Boot y PostgreSQL (pgvector)

Demostración de una aplicación backend desarrollada en **Spring Boot** que implementa un sistema **RAG (Retrieval-Augmented Generation)** utilizando **LangChain4j**, persistencia de memoria conversacional en base de datos y almacenamiento vectorial en **PostgreSQL con pgvector** corriendo en Docker.

## 🚀 Características principales
* **RAG local y eficiente:** Ingesta automática de documentos PDF institucionales (reglamentos, políticas académicas, etc.) al arrancar la aplicación.
* **Procesamiento de PDF:** Uso de *Apache PDFBox* integrado mediante LangChain4j para leer el texto plano de documentos en PDF.
* **Embeddings locales:** Generación de vectores de texto mediante el modelo embebido `all-MiniLM-L6-v2`.
* **Base de datos vectorial:** Almacenamiento y búsqueda de similitud vectorial utilizando **PostgreSQL** con la extensión `vector` (*pgvector*).
* **Memoria de chat persistente:** Historial de conversación de los usuarios almacenado de forma relacional en PostgreSQL.
* **Herramientas (Tools):** Capacidades para que el asistente ejecute funciones personalizadas.

---

## 🛠️ Requisitos previos
1. **Java 17 o superior** instalado.
2. **Docker y Docker Compose** funcionando localmente.
3. Maven (o el wrapper de Maven incluido en el proyecto).

---

## ⚙️ Configuración y Despliegue

### 1. Levantar la base de datos con Docker
En la raíz de tu proyecto, asegúrate de tener configurado tu archivo `docker-compose.yml` para levantar PostgreSQL con soporte para `pgvector` en el puerto `5433`:

```yaml
services:
  postgres-vector:
    image: pgvector/pgvector:pg16
    container_name: postgres_vector_container
    environment:
      POSTGRES_DB: langchain4j_demo
      POSTGRES_USER: tu_usuario
      POSTGRES_PASSWORD: tu_password
    ports:
      - "5433:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```
2. Configurar las credenciales
   En tu archivo `application.properties`, define las variables de conexión:

````properties
spring.datasource.url=jdbc:postgresql://localhost:5433/langchain4j_demo
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
````
3. Agregar documentos PDF
   Coloca tus archivos PDF informativos o reglamentos dentro de la siguiente ruta del proyecto para que la aplicación los cargue y vectorice automáticamente al iniciar:

````plaintext
src/main/resources/documents/
````
# 🚀 Ejecución de la Aplicación
Inicia el proyecto desde tu IDE (IntelliJ IDEA) ejecutando la clase principal `Langchain4jDemoApplication`, o mediante la terminal con Maven:

````bash
mvn spring-boot:run
````
Al arrancar, la aplicación:

1. Inicializará la conexión JPA y el esquema de chat.

2. Leerá los archivos PDF de la carpeta `documents/`.

3. Generará los embeddings y los almacenará automáticamente en la tabla `embeddings` de PostgreSQL (generando 9 fragmentos vectorizados iniciales).

4. Pondrá en marcha el servidor Tomcat en el puerto **8000**.

# 💬 Consultas de Ejemplo (Endpoint de Chat)

Puedes interactuar con el asistente haciendo peticiones HTTP GET directamente desde tu navegador o desde herramientas como Postman.

Formato base:

````plaintext
http://localhost:8000/api/chat?usuarioId=user123&mensaje=<tu_pregunta>
````
Preguntas de prueba recomendadas:
1. Sobre políticas de evaluación:

````plaintext
http://localhost:8000/api/chat?usuarioId=user123&mensaje=¿Cuáles son las políticas de evaluación establecidas para los exámenes?
````
2. Sobre responsabilidades del docente:

```plaintext
http://localhost:8000/api/chat?usuarioId=user123&mensaje=¿Cuáles son las principales responsabilidades del docente según el reglamento?
```
3. Sobre apelaciones académicas:

````plaintext
http://localhost:8000/api/chat?usuarioId=user123&mensaje=¿Cómo se maneja el procedimiento de apelaciones académicas?
````
4. Sobre educación virtual:

````plaintext
http://localhost:8000/api/chat?usuarioId=user123&mensaje=¿Qué normativas o directrices rigen para la educación virtual en la institución?
````

## 🐳 Contenedorización y Docker Compose

Este proyecto incluye soporte completo para Docker y Docker Compose, levantando en simultáneo la aplicación Spring Boot y una base de datos PostgreSQL con soporte vectorial (`pgvector`).

### 1. Prerrequisitos
* [Docker](https://www.docker.com/) y Docker Compose instalados en tu sistema.

### 2. Configuración de Variables de Entorno
Crea un archivo llamado `.env` en la raíz del proyecto basándote en la siguiente estructura (reemplaza los valores con tus credenciales reales y tu API Key de Groq):

```env
DB_USER=postgres
DB_PASSWORD=tu_password_segura
GROQ_API_KEY=gsk_tu_api_key_de_groq_aqui
GROQ_MODEL_NAME=tu_model_empleado
GROQ_BASE_URL=[https://api.groq.com/openai/v1](https://api.groq.com/openai/v1)
```
3. Ejecución del Proyecto
   Para compilar y levantar todo el entorno (la base de datos y la aplicación Spring Boot) en segundo plano, ejecuta:

````bash
docker compose up --build -d
````
Para ver los logs en tiempo real de la aplicación:

````bash
docker compose logs -f langchain4j_app
````
Para detener y limpiar los contenedores:

````bash
docker compose down
````

4. Endpoints de Prueba
   Una vez que el contenedor esté corriendo (`Started Langchain4jDemoApplication`), puedes probar el asistente de IA mediante los siguientes endpoints:

° Chat con memoria persistente:
  **http://localhost:8000/api/chat?usuarioId=user123&mensaje=Hola,%20mi%20nombre%20es%20Christian**

° Verificación de memoria a corto plazo:
  **http://localhost:8000/api/chat?usuarioId=user123&mensaje=¿Cuál%20es%20mi%20nombre?**

° Uso de herramientas (Tools - Cálculos):
  **http://localhost:8000/api/chat?usuarioId=user123&mensaje=Calcula%20el%20promedio%20de%208.5,%209.0%20y%207.5**

## Resumen de la Arquitectura del Agente FAQ
1- Configuración Centralizada (`AiConfig`):

🐾 Se centralizó la inyección de dependencias mediante `@Bean` en una clase de configuración, evitando conflictos de beans con Spring Boot y manteniendo un control estricto sobre los componentes.

🐾 Se configuró el modelo de lenguaje de Groq (`OpenAiChatModel`) con una temperatura de `0.7` para equilibrar creatividad y precisión en las respuestas académicas.

2- Capa RAG y Base de Datos Vectorial (`Pgvector` + `Apache PDFBox`):

🐾 Al iniciar la aplicación, se escanea la carpeta de recursos para procesar automáticamente los documentos PDF institucionales utilizando el parser de PDFBox.

🐾 Los fragmentos de texto (TextSegments) se vectorizan mediante un modelo local ligero (`AllMiniLmL6V2EmbeddingModel`) y se almacenan en una base de datos PostgreSQL utilizando la extensión pgvector.

🐾 El `ContentRetriever` filtra y recupera los fragmentos más relevantes (con umbrales de puntuación definidos) para alimentar contextualmente al modelo.

3- Memoria Conversacional Persistente:

🐾 Se implementó un proveedor de memoria (`ChatMemoryProvider`) respaldado por `PostgresChatMemoryStore`, lo que permite que el agente recuerde el historial de chat de cada usuario (`@MemoryId`) de forma persistente.

4- Orquestación y Herramientas Autónomas (`AsistenteService` & `AsistenteTools`):

🐾 El servicio del agente (`AsistenteService`) define un prompt de sistema estricto que exige un flujo obligatorio: consultar primero el buscador de PDFs (RAG), reportar la metadata y finalmente estructurar una respuesta didáctica citando fuentes (archivo y página).

🐾 El LLM decide de manera autónoma cuándo invocar las herramientas de `AsistenteTools` según la pregunta del estudiante.

````mermaid
graph TD
%% Definición de estilos y componentes
    User([Estudiante / Cliente HTTP]) -->|GET /api/chat?usuarioId=&mensaje=| Controller[Controlador REST]

    subgraph SpringBoot["Spring Boot Application (Docker Container)"]
        Controller --> Asistente[AsistenteService <br/> AiServices.builder]

        subgraph GroqLLM["Groq LLM Layer"]
            Asistente -->|Razonamiento y Orquestación| Groq[Groq Cloud LLM]
        end

        subgraph ToolingRAG["Tooling & RAG"]
            Groq -->|1. Busca en PDFs| Retriever[ContentRetriever]
            Retriever -->|Similitud Vectorial| PgVector[(PostgreSQL + pgvector)]
            Retriever -->|Fragmentos relevantes| Groq
            Groq -->|2. Ejecuta lógica auxiliar| Tools[AsistenteTools <br/> Cálculos, Hora, Reportes]
        end

        subgraph Persistence["Persistence"]
            Asistente -->|Historial de Conversación| Memory[PostgresChatMemoryStore]
            Memory --> PgVector
        end
    end

    Asistente -->|3. Respuesta Final Citada| Controller
    Controller --> User

````