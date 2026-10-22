# ElectroMarket: contexto del proyecto para el asistente de código

Lee este archivo completo antes de escribir o modificar código.

## 1. Qué es el proyecto

ElectroMarket es un sistema web de gestión de inventario (productos, proveedores, entradas y salidas de stock, kardex y alertas de stock mínimo). Es un proyecto universitario grupal (curso Análisis y Diseño de Sistemas de Información, UTP). Mi parte es el backend y la base de datos.

## 2. Sobre mí (importante para cómo debes ayudarme)

- Soy estudiante y estoy **aprendiendo Spring Boot**. Hasta ahora solo he visto Spring Web, Spring Data JPA y Thymeleaf en mi curso. **No he visto Spring Security.**
- Responde **en español**.
- Trabaja **en pasos pequeños**. Antes de escribir código, explica en pocas líneas qué vas a hacer y por qué. Después del código, explica qué hace cada parte importante, como si me lo enseñaras.
- Si hay varias formas de hacer algo, dime cuál elegiste y por qué, sin elegir en silencio.
- **No agregues dependencias nuevas sin preguntarme.**
- **No modifiques el frontend** (`frontend/`) salvo que yo lo pida.

## 3. Estructura del repositorio

```
ProyectoElectromarket/
├── frontend/   React (lo hace otro compañero)
├── backend/    Spring Boot (mi parte)
├── .gitignore
└── README.md
```

El repositorio es **público**. Nunca escribas contraseñas, claves ni datos secretos en ningún archivo que se vaya a subir a Git.

## 4. Tecnologías (versiones reales)

**Backend** (`backend/pom.xml`, proyecto generado con Spring Initializr)
- Java 21, Maven (con Maven Wrapper: usar `.\mvnw.cmd` en Windows)
- Spring Boot 4.1.1
- Dependencias: `spring-boot-starter-webmvc` (API REST), `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `postgresql` (driver 42.7.13)
- **No** se usa Lombok. **No** se usa Thymeleaf. **Spring Security todavía no** (se agregará más adelante, cuando lo vea en mi curso; luego se usará BCrypt para las contraseñas).

**Frontend** (solo para que sepas con qué se comunicará el backend)
- React 18.3.1, TypeScript 5.9.3, Vite 5.4.21, Tailwind CSS 4.3.3, Recharts, Lucide React
- En desarrollo corre en `http://localhost:5173`

**Base de datos**
- PostgreSQL 17, administrada con pgAdmin 4 (9.0)
- Base de datos: `electromarket`, esquema `public`

## 5. Arquitectura

Patrón MVC en versión cliente-servidor:

- **Vista**: la aplicación React, que corre en el navegador.
- **Controlador**: clases `@RestController` de Spring Boot que reciben pedidos de React y devuelven **JSON** (no páginas HTML).
- **Modelo**: entidades JPA, servicios (reglas de negocio) y repositorios (acceso a la base de datos).

Flujo: React → HTTP/JSON → Controller → Service → Repository → PostgreSQL.

Organiza el código por capas en paquetes dentro del paquete base que ya existe en `backend/src/main/java`: `controller`, `service`, `repository`, `entity` y `dto`.

- Los controladores nunca reciben ni devuelven entidades JPA: usan DTOs (paquete `dto`). La respuesta de usuario nunca incluye `contrasena`. El servicio convierte entre entidad y DTO.

Como el frontend y el backend corren en puertos distintos, hay que configurar **CORS** en Spring Boot para permitir `http://localhost:5173` en las rutas `/api/**`. Todas las rutas de la API empiezan con `/api`.

## 6. Base de datos: reglas importantes

**La base de datos ya está creada con un script SQL (la fuente de verdad).** Por eso:

- En `application.properties` se usa `spring.jpa.hibernate.ddl-auto=validate`. **Hibernate nunca debe crear, modificar ni borrar tablas.** Las entidades deben coincidir con las tablas existentes.
- La contraseña de PostgreSQL **no va en un archivo que se suba a Git**. Usa un archivo local ignorado por Git (por ejemplo `application-local.properties`, agregándolo al `.gitignore`) o variables de entorno.
- Conexión local: `jdbc:postgresql://localhost:5432/electromarket`, usuario `postgres`.

### Tablas (7)

| Tabla | Columnas |
|---|---|
| `usuario` | id_usuario (PK, SERIAL), nombre VARCHAR(100), usuario CHAR(50) UNIQUE, contrasena VARCHAR(255), rol VARCHAR(30), fecha_creacion, fecha_actualizacion, usuario_actualizacion (FK a usuario, nulo) |
| `proveedor` | id_proveedor (PK), ruc CHAR(11) UNIQUE, razon_social VARCHAR(150), contacto VARCHAR(100) nulo, tiempo_entrega INTEGER nulo, fecha_creacion, fecha_actualizacion, usuario_actualizacion (FK a usuario, nulo) |
| `producto` | id_producto (PK), codigo_sku CHAR(20) UNIQUE, nombre VARCHAR(150), categoria VARCHAR(50), marca VARCHAR(50) nulo, precio NUMERIC(10,2), ubicacion VARCHAR(50) nulo, stock_minimo INTEGER default 0, stock_actual INTEGER default 0, fecha_creacion, fecha_actualizacion, usuario_actualizacion (FK a usuario, nulo) |
| `entrada_stock` (cabecera) | id_entrada (PK), id_proveedor (FK), id_usuario (FK, quien registró), documento VARCHAR(20), fecha DATE default hoy, estado VARCHAR(20) default 'REGISTRADA', fecha_creacion, fecha_actualizacion, usuario_actualizacion (FK a usuario, nulo) |
| `detalle_entrada` | id_detalle (PK), id_entrada (FK), id_producto (FK), cantidad INTEGER, fecha_creacion |
| `salida_stock` | id_salida (PK), id_producto (FK), id_usuario (FK), fecha DATE default hoy, tipo_salida VARCHAR(20), cantidad INTEGER, fecha_creacion |
| `kardex` | id_kardex (PK), id_producto (FK), fecha_calculo DATE default hoy, stock_inicial, entradas default 0, salidas default 0, stock_final, fecha_creacion |

Relaciones (todas 1:N): proveedor→entrada_stock, usuario→entrada_stock, entrada_stock→detalle_entrada, producto→detalle_entrada, producto→salida_stock, usuario→salida_stock, producto→kardex. Una entrada puede tener varios productos (cabecera-detalle); una salida es de un solo producto.

Todas las claves primarias son `SERIAL` (autoincrementales). Todas las tablas son en minúsculas con guion bajo (`entrada_stock`, `detalle_entrada`...).

### Consideraciones para las entidades JPA

- `SERIAL` → `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- `NUMERIC(10,2)` → `BigDecimal`. `DATE` → `LocalDate`. `TIMESTAMP` → `LocalDateTime`.
- Los campos `CHAR(n)` (`usuario.usuario`, `proveedor.ruc`, `producto.codigo_sku`) rellenan con espacios hasta completar el largo; al leerlos puede hacer falta `.trim()`.
- `fecha_creacion` y `fecha_actualizacion` los llena la **base de datos** (valor por defecto y trigger). Las entidades **no deben enviarlos**: márcalos como `insertable = false, updatable = false` para no mandar `NULL` en los INSERT.
- Hay un trigger de PostgreSQL que actualiza `fecha_actualizacion` en `usuario`, `proveedor`, `producto` y `entrada_stock`, pero **`usuario_actualizacion` no lo llena la base de datos**: lo debe enviar el backend en cada UPDATE (id del usuario que hizo el cambio).
- **`producto.stock_actual` no se actualiza solo.** El backend debe actualizarlo dentro de una transacción (`@Transactional`) cada vez que se registre una entrada (suma) o una salida (resta).

## 7. Reglas de negocio (requerimientos)

- **Roles:** Administrador y Almacén.
- **Productos:** código SKU único, nombre, categoría, marca, precio, ubicación, stock mínimo.
- **Entradas de stock:** se registran asociadas a una guía de remisión o factura (campo `documento`), con un proveedor y una o más líneas de producto.
- **Salidas de stock:** de un solo producto. En la pantalla del frontend el campo se llama "Motivo" y tiene tres opciones: "Venta", "Traslado" y "Merma / Deterioro". En la base de datos se guarda en la columna `tipo_salida` con estos códigos, siempre en mayúsculas: `VENTA`, `TRASLADO` y `MERMA`. El frontend muestra el texto legible y el backend recibe y guarda solo el código (el backend debe rechazar cualquier otro valor). (El requerimiento RF04 del documento dice "transferencia", pero el diseño actual usa "traslado".) No se puede sacar más stock del que hay: el stock nunca debe quedar negativo.
- **Alerta de stock mínimo:** un producto está en alerta cuando `stock_actual` es menor o igual a `stock_minimo`.
- **Proveedores:** RUC (11 dígitos), razón social, contacto, tiempo de entrega.
- **Kardex:** guarda saldos ya calculados por producto y fecha (existe para que las consultas de stock sean rápidas).
- **Reportes** de Kardex e inventario, exportables a PDF y Excel (más adelante).
- **Rendimiento:** las consultas y búsquedas de stock deben responder en menos de 2 segundos.

## 8. Orden de trabajo previsto

1. Configurar la conexión a PostgreSQL y lograr que el backend arranque sin errores.
2. Crear las entidades JPA de las 7 tablas.
3. Crear los repositorios.
4. API de productos (listar, buscar, crear, editar) con CORS configurado, y probar con el navegador o con una herramienta de pruebas de API.
5. API de proveedores.
6. API de entradas (cabecera + detalle) actualizando el stock en una transacción.
7. API de salidas, validando que no quede stock negativo.
8. Kardex y alertas de stock mínimo.
9. Más adelante: login con Spring Security (cuando lo vea en mi curso) y reportes PDF/Excel.

Empezamos por el paso 1, salvo que yo indique otro.
