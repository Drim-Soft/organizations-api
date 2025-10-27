# Pruebas Unitarias - Organizations API

Este proyecto incluye un conjunto completo de pruebas unitarias e integración para la API de organizaciones usando JUnit 5, Mockito y Spring Boot Test.

## Estructura de Pruebas

### Modelos (Model Tests)
- **OrganizationTest.java**: Pruebas para el modelo Organization
  - Getters y setters
  - Relaciones con usuarios
  - Validación de campos
  - Manejo de valores nulos

- **UserPlanifikaTest.java**: Pruebas para el modelo UserPlanifika
  - Getters y setters
  - Relación con organización
  - Validación de campos
  - Manejo de valores nulos

### Repositorios (Repository Tests)
- **OrganizationRepositoryTest.java**: Pruebas de integración para OrganizationRepository
  - Operaciones CRUD básicas
  - Búsquedas por ID
  - Eliminación de registros
  - Actualización de datos

- **UserPlanifikaRepositoryTest.java**: Pruebas de integración para UserPlanifikaRepository
  - Operaciones CRUD básicas
  - Relaciones con organizaciones
  - Búsquedas y eliminaciones

### Servicios (Service Tests)
- **OrganizationServiceTest.java**: Pruebas unitarias para OrganizationService
  - Métodos de negocio con mocks
  - Casos de éxito y error
  - Vinculación de usuarios a organizaciones
  - Manejo de excepciones

### Controladores (Controller Tests)
- **OrganizationControllerTest.java**: Pruebas de integración para OrganizationController
  - Endpoints REST
  - Códigos de respuesta HTTP
  - Serialización/deserialización JSON
  - Manejo de errores

### Pruebas de Integración
- **OrganizationIntegrationTest.java**: Pruebas de integración completas
  - Flujos completos de trabajo
  - Interacción entre capas
  - Persistencia de datos
  - Transacciones

## Configuración de Pruebas

### Base de Datos de Pruebas
- **H2 Database**: Base de datos en memoria para pruebas
- **Perfil de prueba**: `application-test.properties`
- **DDL automático**: `create-drop` para limpieza entre pruebas

### Dependencias de Pruebas
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## Ejecución de Pruebas

### Ejecutar todas las pruebas
```bash
mvn test
```

### Ejecutar pruebas específicas
```bash
# Solo pruebas unitarias
mvn test -Dtest="*Test"

# Solo pruebas de integración
mvn test -Dtest="*IntegrationTest"

# Pruebas de un paquete específico
mvn test -Dtest="com.planifikausersapi.planifikausersapi.service.*Test"
```

### Ejecutar desde IDE
- **IntelliJ IDEA**: Click derecho en la clase de prueba → "Run Test"
- **Eclipse**: Click derecho en la clase de prueba → "Run As" → "JUnit Test"
- **VS Code**: Usar la extensión de Java Test Runner

## Cobertura de Pruebas

Las pruebas cubren:

### ✅ Casos de Éxito
- Operaciones CRUD completas
- Relaciones entre entidades
- Endpoints REST
- Flujos de negocio

### ✅ Casos de Error
- Recursos no encontrados
- Datos inválidos
- Operaciones fallidas
- Manejo de excepciones

### ✅ Casos Límite
- Valores nulos
- Cadenas vacías
- IDs inexistentes
- Relaciones rotas

## Mejores Prácticas Implementadas

1. **Arrange-Act-Assert**: Estructura clara en todas las pruebas
2. **Mocks apropiados**: Uso de Mockito para dependencias externas
3. **Datos de prueba**: Objetos de prueba reutilizables
4. **Aislamiento**: Cada prueba es independiente
5. **Nombres descriptivos**: Nombres que explican qué se está probando
6. **Configuración separada**: Perfil de prueba independiente

## Ejemplos de Uso

### Prueba Unitaria Simple
```java
@Test
void testOrganizationCreation() {
    Organization org = new Organization();
    org.setName("Test Company");
    org.setNit("12345678-9");
    
    assertEquals("Test Company", org.getName());
    assertEquals("12345678-9", org.getNit());
}
```

### Prueba con Mock
```java
@Test
void testFindOrganizationById() {
    when(organizationRepository.findById(1L))
        .thenReturn(Optional.of(organization));
    
    Optional<Organization> result = organizationService.findById(1L);
    
    assertTrue(result.isPresent());
    assertEquals("Test Company", result.get().getName());
}
```

### Prueba de Integración
```java
@Test
void testCreateOrganizationViaAPI() throws Exception {
    mockMvc.perform(post("/organizations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(organization)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Company"));
}
```

## Troubleshooting

### Problemas Comunes

1. **Error de conexión a BD**: Verificar configuración de H2 en `application-test.properties`
2. **Tests fallan**: Verificar que todas las dependencias estén en el classpath
3. **Mocks no funcionan**: Verificar anotaciones `@Mock` y `@InjectMocks`
4. **Transacciones**: Usar `@Transactional` en pruebas de integración

### Logs de Depuración
```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```
