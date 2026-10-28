# TPO Paradigma Orientado a Objetos — estado del proyecto

Grupo 3: Lucas Florentin, Giovanni Oriolani, Santiago González, Máximo Zaragoza.

Este documento reemplaza a los `TPO_POO_contexto_*.txt` que estaban en el Drive, que
quedaron de la Fase A y ya no describen el proyecto.


## Qué es

Un roguelike de combates por turnos en Java 17 con Swing. El jugador arma una party de
**3 personajes** entre **6 clases** y sube **20 etapas**: combates, cuatro tiendas, un
minijefe en la 10 y un jefe final en la 20. Si la party cae, la run se termina y hay
que empezar de nuevo.


## Dónde estamos

**El modelo está completo: 87 clases y 128 tests de JUnit 5 que pasan.** Compila sin
un solo warning con `-Xlint:all,-serial`.

| Paquete | Clases | Qué hay |
|---|---|---|
| `modelo.combatientes` | 22 | `Combatiente`, `Personaje` y las 6 clases jugables, `Enemigo` y los 11 enemigos |
| `modelo.combatientes.objetivos` | 4 | las interfaces de apuntado |
| `modelo.items` | 16 | `Item`, `Consumible`, `Equipable`, `Inventario` y los dos catálogos |
| `modelo.movimientos` | 15 | `Accion`, `Movimiento` y sus familias |
| `modelo.efectos` | 9 | `EfectoEstado` y sus 8 subclases |
| `modelo.partida` | 9 | `Juego`, `Partida`, `Party`, las etapas y el generador |
| `modelo.tienda` | 5 | `Tienda`, `GeneradorDeStock`, `MaquinaDeSlots` |
| `modelo.combate` | 5 | `Combate`, `GestorDeTurnos`, `CalculadorDeDaño` |
| `persistencia` | 2 | la interfaz del repositorio y su implementación en archivo |

**Falta la vista en Swing y los controladores.** El profesor había dicho de no tocar
la vista hasta tener el modelo, y ya está.

Los cuatro diagramas de clases y los dos de secuencia están en `diagramas/`, al día
con el código: verificamos clase por clase y método por método que no haya
desfasaje.


## Cómo se decidieron los números

Esto es lo que más nos diferencia y conviene saber explicarlo: **ninguno de los
números del juego está puesto a ojo**. Se armó un simulador descartable que juega
partidas completas instanciando las clases reales del proyecto —las mismas seis
clases jugables, el `CalculadorDeDaño`, la `Tienda`, el `CatalogoDeEquipables`, la
`MaquinaDeSlots`— y se corrieron miles de partidas por cada configuración hasta que la
curva de dificultad dio bien.

Resultado actual: **19% de partidas perdidas** con una party de Guerrero, Mago y
Curador. Las ocho composiciones que se probaron terminan el juego.

Los números y su justificación están en cuatro documentos:

| Documento | Qué tiene |
|---|---|
| `BALANCE_ENEMIGOS.md` | los 11 enemigos, sus movimientos y el escalado por etapa |
| `EQUIPABLES.md` | los 36 equipables, los precios y la economía del oro |
| `CONSUMIBLES.md` | los 5 consumibles y la comparación con la curación del Curador |
| `MAQUINA_DE_SLOTS.md` | símbolos, tabla de premios y el retorno exacto |

Dos hallazgos que vale la pena tener a mano, porque son el tipo de cosa que se
pregunta:

**Sin pociones y sin Curador, el juego es invencible.** Las composiciones sin Curador
pierden el **100%** de las partidas si no se pueden comprar pociones. Por eso la
Poción de Vida cuesta solo 25 y cura el 55% de la vida máxima: no es un objeto de
apoyo, es lo único que puede reemplazar a una clase entera.

**El oro gastado en la tienda avisa del desbalance antes que el porcentaje de
derrotas.** Con enemigos demasiado fuertes la party muere temprano, junta menos oro,
compra menos equipo y muere todavía antes. En las corridas rotas gastaba unos 500 de
oro en equipamiento; en las buenas, 1126.


## Las decisiones de diseño que hay que saber defender

**Los 24 movimientos salen de 4 clases.** `AtaqueFisico`, `AtaqueMagico`,
`HabilidadDeCuracion` y `HabilidadDeEstado`. Ni el ataque básico ni la definitiva son
clases aparte: el básico es un ataque con costo 0 y la definitiva es cualquier
movimiento con `esDefinitivo = true`. La variedad sale de los datos, no de multiplicar
clases.

**Los efectos de estado se consultan por comportamiento, no por nombre.**
`EfectoEstado` tiene tres hooks con implementación por defecto:
`getModificadorPorcentual()`, `absorber()` e `impideActuar()`. Nadie compara el
nombre de un efecto contra un texto. Los buffos **no escriben** las estadísticas del
combatiente: las informan, y los getters efectivos suman los porcentajes. Por eso al
expirar no hay nada que revertir, que es donde este tipo de sistemas suele romperse.

**Las cuatro interfaces de apuntado tienen métodos `default`.** `ObjetivoMasDebil`,
`ObjetivoMasPeligroso`, `ObjetivoAleatorio` y `ObjetivoSinEfecto`. Cada enemigo
declara en su firma a quién apunta, y la lógica se escribe una sola vez. El
`SeñorDeLasSombras` implementa dos y desambigua con `Interfaz.super.elegirObjetivo()`
según la fase.

**El Nigromante no conoce al Combate.** Devuelve sus esqueletos dentro de un
`ResultadoAccion` y es `Combate` quien los incorpora. Si fuera al revés, cada enemigo
necesitaría una referencia al combate que lo está jugando y habría dependencia
circular entre los paquetes.

**La resistencia a efectos es un número, no un `esJefe()`.** `Combatiente` tiene
`resistenciaAEstados`, que vale 0 en casi todos y 50 en los dos jefes. Un aturdimiento
del 40% les entra al 20%. Sin `instanceof` y graduable.

**El Berserker es el mejor ejemplo de polimorfismo del trabajo.** No tiene ningún
método propio que el motor deba conocer: sobrescribe `getAtaqueFisicoEfectivo()`, que
ya existía, y `CalculadorDeDaño` sigue llamando al mismo getter sin enterarse de que
existe una clase Berserker.

**Todo lo que se hace en un turno pasa por `Accion`.** Es el patrón Command. El
jugador arma su acción desde la pantalla y el enemigo la arma solo en su
`elegirAccion()`; `Combate` no los distingue, solo dice `ejecutar()`.

**La recompensa la define la etapa, no el enemigo.** Si viniera por enemigo, el
Nigromante sería una fuente infinita de experiencia y oro, porque invoca sin parar.

**Lo que se guarda es solo la `Partida`.** De ahí cuelgan la party, los personajes con
su nivel y su equipo, los efectos activos y el inventario. La etapa no se guarda: al
cargar se vuelve a generar a partir del número.


## GRASP y SOLID, con la clase al lado

**Information Expert.** Los getters efectivos de `Combatiente` calculan base más
equipamiento más efectos; nadie de afuera calcula las estadísticas de nadie.
`Party.repartirExperiencia()` reparte y cada `Personaje` decide solo si le alcanza
para subir de nivel.

**Creator.** `EtapaDeCombate.crearCombate()` crea el `Combate` porque es quien tiene
los enemigos. `Combate.ejecutarMovimiento()` crea la `Accion` porque tiene los datos.
El `Nigromante` crea sus `Esqueleto`.

**Controller.** `Juego` recibe los eventos del sistema y coordina delegando. Vive en el
modelo: no tiene un solo método que muestre, pinte o reciba clicks.

**Polymorphism.** Los 11 `elegirAccion()`. `Etapa.otorgaExperiencia()` y
`esDeCombate()`, que evitan cualquier `instanceof` en `Juego`.

**Pure Fabrication.** `GestorDeTurnos`, `CalculadorDeDaño`, `GeneradorDeEtapas` y
`GeneradorDeStock`: ninguno existe en el mundo del juego, pero cada uno le saca de
encima a otra clase una responsabilidad que no le correspondía.

**Protected Variations.** `RepositorioPartidas` es una interfaz: ninguna clase del
juego sabe que se guarda en un archivo. Pasar a JSON o a una base de datos es escribir
otra implementación.

**Responsabilidad única.** `Combate` no calcula daño ni ordena turnos: delega.

**Abierto/cerrado.** Agregar un enemigo, un efecto, un consumible o un tipo de etapa
es agregar una clase, sin tocar las que ya existen.

**Sustitución de Liskov.** Cualquier `Combatiente` sirve donde se espera uno: el motor
no distingue entre un `Personaje` y un `Enemigo`.

**Segregación de interfaces.** Cuatro interfaces de apuntado de un método cada una en
vez de una grande: el `Hechicero` no implementa ninguna porque pega en área.

**Inversión de dependencias.** `Juego` depende de `RepositorioPartidas`, no de
`RepositorioPartidasArchivo`.


## Convenciones

Están en `CONVENCIONES.md` y hay que leerlas antes de programar. Lo más importante:

- **Java 17**, todo en UTF-8, y el proyecto compila **sin un solo warning**.
- **Va ñ en todo**, nombres de tipo incluidos: `TipoDaño`, `CalculadorDeDaño`,
  `SeñorDeLasSombras`, `Emponzoñador`. Está probado en las cuatro máquinas. La única
  excepción son los valores de enum, que van en mayúsculas.
- **Los mismos nombres que en los diagramas.** Si hay que renombrar algo, se cambia
  también en el diagrama y se avisa.
- **Nada de azar sin semilla en los tests.**


## Qué falta

1. La **vista en Swing** y los controladores.
2. Decidir dos cosas que quedaron anotadas en `BALANCE_ENEMIGOS.md`: que la party
   nunca pasa de nivel 13 aunque el máximo sea 20, y que la definitiva se desbloquea
   una etapa después del minijefe.
3. Rehacer la **guía de defensa** y la **explicación del diagrama 4**, que son de
   antes de todo el código.
