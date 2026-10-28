# JuegoPOO: RPG por turnos

Trabajo práctico de **Paradigma Orientado a Objetos** (UADE), Grupo 03.

Roguelike de combates por turnos: el jugador arma una party de 3 personajes entre 6 clases y sube 20 etapas con combates, tiendas, un minijefe y un jefe final.

Integrantes: Máximo Zaragoza, Santiago González, Giovanni Oriolani y Lucas Florentin.

## Requisitos

- **JDK 17 o superior.** Swing viene incluido, no hay que instalar nada más.
- Un IDE: IntelliJ IDEA, Eclipse o VS Code (con el Extension Pack for Java).

El proyecto usa **Maven** para compilar. Los tres IDEs lo reconocen solos, no hace falta instalarlo aparte.

## Cómo abrirlo

1. Clonar el repo:
   ```
   git clone https://github.com/lukitas-jpg/JuegoPOO.git
   ```
2. Abrir la carpeta en el IDE:
   - **IntelliJ:** *File → Open* y elegir la carpeta (detecta el `pom.xml`).
   - **Eclipse:** *File → Import → Maven → Existing Maven Projects*.
   - **VS Code:** *File → Open Folder*.
3. Ejecutar `mvn test`. Tienen que pasar todos.
4. Para jugar, ejecutar `ar.edu.uade.rpg.App`.

Desde la terminal, con Maven instalado:
```
mvn package
java -jar target/rpg.jar
```

## Estructura

```
src/main/java/ar/edu/uade/rpg/
├── App.java              Punto de entrada: arma modelo, vistas y controladores
├── modelo/               M de MVC: reglas del juego, sin nada de Swing
│   ├── combatientes/     Diagrama 1: Combatiente, Personaje, Enemigo y los 6 enemigos
│   │   └── objetivos/    Diagrama 1: las 4 interfaces de apuntado
│   ├── movimientos/      Diagrama 2: Accion, Movimiento, Ataque, Habilidad
│   ├── efectos/          Diagrama 2: EfectoEstado (veneno, escudo, buffs...)
│   ├── items/            Diagrama 3: Item, Consumible, Equipable, Inventario y catálogos
│   ├── tienda/           Diagrama 3: Tienda y GeneradorDeStock
│   ├── combate/          Diagrama 4: Combate, GestorDeTurnos, CalculadorDeDaño
│   └── partida/          Diagrama 4: Juego, Partida, Party, Etapas
├── controlador/          Diagrama 5: los controladores del MVC y la interfaz VistaDelJuego
├── dto/                  Diagrama 5: los datos que viajan a la vista (records)
├── vista/                Diagrama 5: la ventana Swing y sus 6 pantallas
└── persistencia/         RepositorioPartidas y su implementación en archivo

src/main/resources/imagenes/   Las 89 imágenes del juego (ver su LEEME.md)
src/test/java/            Tests con JUnit 5 (se corren con mvn package)
diagramas/                Diagramas UML (.puml, .png y .svg)
herramientas/             Script que dibuja los equipables propios (opcional, Python)
LICENCIAS.md              De dónde sale cada recurso y su licencia
```

Cada paquete tiene un `package-info.java` que lista qué clases del diagrama van ahí.

Las partidas guardadas van a la carpeta `saves/`, que git ignora.

## Estado

**El juego se puede jugar de punta a punta.** Modelo completo (diagramas 1 a 4),
vista Swing y controladores (diagrama 5), con tests de JUnit 5 que pasan. Hay tests
que juegan runs enteras con peleas de verdad, tanto contra el modelo como a través
de los controladores con una vista falsa.

Pantallas: inicio, selección de la party, batalla, resultado, estado de la party (con
inventario) y tienda. Ver `diagramas/S3_mapa_de_pantallas`.

La vista de esta versión salió de la versión "jugable" que se armó aparte, pasada a
este modelo. El modelo de aquella versión no se usa.

## Documentación

Los diagramas (`.puml`, `.png` y `.svg`) están en la carpeta [`diagramas/`](diagramas/).

Los números del juego están medidos por simulación y documentados:

| Documento | Qué tiene |
|---|---|
| [CONVENCIONES.md](CONVENCIONES.md) | Cómo escribimos el código. **Leer antes de programar.** |
| [BALANCE_ENEMIGOS.md](BALANCE_ENEMIGOS.md) | Los 6 enemigos: estadísticas, movimientos y escalado por etapa |
| [EQUIPABLES.md](EQUIPABLES.md) | Los 36 equipables, los precios, la economía del oro y el reroll |
| [CONSUMIBLES.md](CONSUMIBLES.md) | Los 5 consumibles y por qué la poción vale lo que vale |

Antes de programar, leer [CONVENCIONES.md](CONVENCIONES.md).

## Cómo trabajamos con el repo

- Nadie trabaja sobre una copia suelta: siempre `git pull` antes de empezar.
- Cada tarea en su rama (`git checkout -b tienda-en-la-vista`) y se integra con un pull request.
- Antes de subir: `mvn test` tiene que pasar.
- Si cambia una firma, el diagrama se actualiza en el mismo commit. La consigna exige que coincidan.

## Créditos de las imágenes

Todo CC0. Personajes, enemigos y fondos: *16x16 DungeonTileset II* de 0x72 (el Asesino
y el Curador, con los colores cambiados). Íconos: *496 pixel art icons for medieval/fantasy
RPG* de Henrique Lazarini (7Soul1), en OpenGameArt. Los 36 dibujos propios
de los equipables y el ícono de Protección son dibujos propios. Detalle, links y licencias en
[LICENCIAS.md](LICENCIAS.md).
