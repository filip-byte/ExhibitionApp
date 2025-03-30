# ExhibitionApp Backend

Welcome to **ExhibitionApp**—a Spring Boot backend for curating a virtual art galleries! This API lets user search artworks from the Art Institute of Chicago (Artic) and The Metropolitan Museum of Art (MET), create galleries, and add artworks to them. Built with Java, Spring Boot, and PostgreSQL, it’s a lightweight, RESTful service ready.

## Features

- Search artworks from Artic and MET APIs by keyword.
- Create unique galleries with names and descriptions.
- Add artworks to galleries (no duplicates allowed!).
- View all galleries or artworks in a specific gallery.
- Clean, tested code with 17 passing unit and integration tests.

## Tech Stack

- **Java**: 21.
- **Spring Boot**: For RESTful API goodness.
- **PostgreSQL**: Stores galleries and artworks.
- **Maven**: Dependency management and build tool.

## Prerequisites

- Java JDK (>=21 recommended).
- Maven.
- PostgreSQL.

## Setup

### 1. Clone the Repo
```bash
git clone https://github.com/filip-byte/ExhibitionApp.git
cd ExhibitionApp
```

### 2. Configure PostgreSQL

#### Create a database named `curation1`
```bash
psql -U postgres
```
```sql
CREATE DATABASE curation1;
\q
```

#### Update `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/curation1
spring.datasource.username=jv_user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```
Or open in IntelliJ and run `ExhibitionAppApplication`.

### 4. Test It
- API runs at `http://localhost:8080`

## API Endpoints

All endpoints are under `/api/artworks`.

| Method | Endpoint | Description | Request Params | Response |
|--------|---------|-------------|----------------|----------|
| GET | `/api/artworks` | Search artworks from Artic/MET | `source` (Artic/MET), `q` (query), `page` (default: 1), `limit` (default: 5) | List of `{id, title, imageUrl}` |
| POST | `/api/artworks/galleries` | Create a new gallery | `name` (required), `description` (optional) | `{id, name, description}` |
| POST | `/api/artworks/galleries/{id}/artworks` | Add artwork to a gallery | `id` (path), `imageUrl` (required) | None (200 OK) |
| GET | `/api/artworks/galleries/{id}` | List artworks in a gallery | `id` (path) | List of `{id, title, imageUrl}` |
| GET | `/api/artworks/galleries` | List all galleries | None | List of `{id, name, description}` |

## Example Usage (Postman)

### Search Artworks:
```bash
GET "http://localhost:8080/api/artworks?source=Artic&q=cats"
```

### Create Gallery:
```bash
POST "http://localhost:8080/api/artworks/galleries?name=MyGallery&description=Cat%20Art"
```

### Add Artwork:
```bash
POST "http://localhost:8080/api/artworks/galleries/1/artworks?imageUrl=https://test.jpg"
```

### List Galleries:
```bash
GET "http://localhost:8080/api/artworks/galleries"
```

## Database

### Tables:
- `gallery`: Stores `id`, `name`, `description`.
- `gallery_artwork`: Stores `id`, `gallery_id`, `image_url` (unique per gallery).

### Reset Data:
```bash
psql -U jv_user -d curation1 -h localhost -W
```
```sql
TRUNCATE TABLE gallery_artwork, gallery RESTART IDENTITY CASCADE;
\q
```

