# Organización de Clases en TurnosrotativosApplication

## Estructura de Directorios

El proyecto está organizado en las siguientes carpetas principales:

- `src/main/java/com/neoris/turnosrotativos/`
  - `controllers/`: Contiene los controladores de la aplicación.
  - `services/`: Contiene los servicios de la aplicación.
  - `entities/`: Contiene las clases que representan los objetos de datos.
  - `repository/`: Contiene las interfaces de los repositorios de datos.
  - `exceptions/`: Contiene las custom exceptions y un manejador de excepciones
- `/entities/dto/`: Contiene el Data Transfer Object (DTO) utilizado para intercambiar datos entre capas.

## Modulo Empleado

	Entity:
	- src/main/java/com/neoris/turnosrotativos/entities/Empleado:
	Contiene los datos del empleado a persistir.
	
	Controller:
	- src/main/java/com/neoris/turnosrotativos/controllers/EmpleadoController:
	EmpleadoController es el encargado de invocar los metodos correspondientes de EmpleadoService a travez de la inyeccion de dependencia para gestionar las solicitudes Http
	Las bad request generadas son manejadas por la clase CustomExceptionHandler

	Service:
	- src/main/java/com/neoris/turnosrotativos/services/EmpleadoService:
	EmpleadoService es el encargado de recibir las solicitudes del controlador e invocar al metodo correspondiente para responderlas, es el encargado de
	manejar las validaciones, la logica de negocio asociada y disparar la CustomException correspondiente con un mensaje personalizado. Tambien tiene metodos
	para comunicar consultas simples como obtenerInfoEmpleado para comunicarse con otras capas, asi como tambien es su responsabilidad invocar de EmpleadoRepository 
	a travez de la inyeccion de dependencia para solicitar la persistencia de los datos.

	Repository:
	- src/main/java/com/neoris/turnosrotativos/repositories/EmpleadoRepository:
	EmpleadoRepository es el encargado de manejar la persistencia de la entity Empleado, y provee query methods personalizados para las solicitudes de EmpleadoService

## Modulo Concepto

	Entity:
	- src/main/java/com/neoris/turnosrotativos/entities/Concepto:
	Contiene los datos del concepto a persistir.
	
	Controller:
	- src/main/java/com/neoris/turnosrotativos/controllers/ConceptoController:
	ConceptoController es el encargado de invocar los metodos correspondientes de ConceptoService a travez de la inyeccion de dependencia para gestionar las solicitudes Http
	Las bad request generadas son manejadas por la clase CustomExceptionHandler

	Service:
	- src/main/java/com/neoris/turnosrotativos/services/ConceptoService:
	ConceptoService es el encargado de recibir e invocar al metodo correspondiente para responder a las solicitudes del ConceptoController, asi como tambien la implementacion de metodos
	para comunicar consultas simples como buscarConceptoPorId para comunicarse con otras capas, ademas es su responsabilidad invocar los metodos de EmpleadoRepository 
	a travez de la inyeccion de dependencia para solicitar la persistencia de los datos.
.
	
	Repository:
	- src/main/java/com/neoris/turnosrotativos/repositories/ConceptoRepository:
	ConceptoRepository extiende CrudRepository y es el encargado de manejar la persistencia de la entity Concepto y provee query methods personalizados para las solicitudes de ConceptoService

## Modulo Jornada

	Entity:
	- src/main/java/com/neoris/turnosrotativos/entities/Jornada:
	Contiene los datos de la jornada a persistir.
	
	Controller:
	- src/main/java/com/neoris/turnosrotativos/controllers/JornadaController:
	JornadaController es el encargado de invocar los metodos correspondientes de JornadaService a travez de la inyeccion de dependencia para gestionar requests Http y sus respectivos Query Parameters
	Las bad request generadas son manejadas por la clase CustomExceptionHandler

	Service:
	- src/main/java/com/neoris/turnosrotativos/services/ConceptoService:
	JornadaService es el encargado de recibir e invocar al metodo correspondiente para responder a las solicitudes del JornadaController.
	JornadaService es capaz de gestionar la logica de negocio para la validacion de creacion de jornada y a las consultas del JornadaController.
	Esto es posible a travez de inyeccion de dependencia con los EmpleadoService, ConceptoService y a los query methods proveidos por JornadaRepository.
	
	Repository:
	- src/main/java/com/neoris/turnosrotativos/repositories/ConceptoRepository:
	JornadaRepository extiende CrudRepository y es el encargado de manejar la persistencia de la entity Jornada y proveer query methods personalizados para las solicitudes de JornadaService
	

## Modulo CustomExceptionHandler

	CustomExceptionHandler:
	- src/main/java/com/neoris/turnosrotativos/exceptions/CustomExceptionHandler:
	CustomExceptionHandler es un manejador de excepciones global personalizado para la aplicacion, provee de los metodos que capturan las excepciones de formato tipo @valid
	devolviendo el mensaje asociado a la logica de negocio en los requerimientos de la aplicacion.
	Tambien se encarga de capturar especificamente las excepciones de las clases BadRequestException, ConflictoException y RecursoNoEncontradoException y su mensaje asociado
	y devolver la response correspondiente a los http status a los que hacen referencia en el nombre, proveyendo asi la posibilidad de cargar mensajes personalizados
	con su request de error correspondiente en cualquier parte de la aplicacion.

	RecursoNoEncontradoException:
	- src/main/java/com/neoris/turnosrotativos/exceptions/RecursoNoEncontradoException:
	Esta es una custom Exception que se puede crear como respuesta a un error de validacion o logica de negocio donde no se encuentra algun recurso. Devuelve el http status not found
	y el mensaje personalizado asociado al momento de creacion.

	ConflictoException:
	- src/main/java/com/neoris/turnosrotativos/exceptions/ConflictoException:
	Esta es una custom Exception que se puede crear como respuesta a un error de validacion o logica de negocio donde se encuentra algun conflicto. Devuelve el http status conflict
	y el mensaje personalizado asociado al momento de creacion.

	BadRequestException:
	- src/main/java/com/neoris/turnosrotativos/exceptions/ConflictoException:
	Esta es una custom Exception que se puede crear como respuesta a un error de validacion o logica de negocio donde se ingresan mal los datos para una solicitud. Devuelve el http status conflict
	y el mensaje personalizado asociado al momento de creacion.

## Consideraciones de diseño:
	
	En un principio al tener response bodys iguales a lo que se necesitaba para gestionar el empleado y el concepto, se considero no necesario implementar DTOs
	para ahorar tiempo, conforme se fue avanzando en el proyecto, llegando a la entidad Jornada, me vi obligado a utilizar un DTO y luego de trabajar con el
	me di cuenta de que era mas ordenado y legible trabajar con DTO, mas alla de las buenas practicas, considero que seria una mejora al codigo si todas las entidades
	tuvieran su DTO propio, elegi de esta manera priorizar el esfuerzo de la aplicacion en cumplir con todas las solicitudes de las user story proporcionadas y no
	en refactorizar dentro de los metodos donde se invoca a los atributos de la entidad, sin embargo en jornada si esta bien aplicado el concepto de DTO.
	
	El CustomExceptionHandler surgio de la necesidad de manejar diferentes mensajes para diferentes situaciones, al aprovechar de que hacer un throw new y cual sea
	la excepcion custom con cual status quieras devolver, asociando de esta manera a los metodos de validacion con el error que se desea comunicar. Estos metodos de validacion
	son invocados por un metodo void, estos metodos de logica de negocio se ejecutan uno atras del otro y si no disparan la excepcion la creacion se ejecuta, se llama al repositorio
	asociado, se persiste el dato en la base de datos y se devuelve el http status correspondiente.

	En el codigo se agregaron comentarios para explicar la logica detras de los metodos que no son intuitivos.

	Inicialmente la idea de la aplicacion siempre fue aplicar buenas practicas de diseño, sin embargo al construirlo queda evidente que obtener las habilidades para realizar una limpia
	implementacion y diseño con buenas practicas es un proceso de prueba y error, por lo que el codigo todavia podria mejorarse.
	

	
	