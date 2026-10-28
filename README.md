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
3. Ejecutar `mvn test`. Tienen que pasar los 128.

Mientras no exista la vista, `ar.edu.uade.rpg.App` abre una ventana que dice
"Proyecto configurado correctamente": sirve para confirmar que Swing arranca.

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
│   ├── combatientes/     Diagrama 1: Combatiente, Personaje, Enemigo y los 11 enemigos
│   │   └── objetivos/    Diagrama 1: las 4 interfaces de apuntado
│   ├── movimientos/      Diagrama 2: Accion, Movimiento, Ataque, Habilidad
│   ├── efectos/          Diagrama 2: EfectoEstado (veneno, escudo, buffs...)
│   ├── items/            Diagrama 3: Item, Consumible, Equipable, Inventario y catálogos
│   ├── tienda/           Diagrama 3: Tienda, GeneradorDeStock, MaquinaDeSlots
│   ├── combate/          Diagrama 4: Combate, GestorDeTurnos, CalculadorDeDaño
│   └── partida/          Diagrama 4: Juego, Partida, Party, Etapas
├── vista/                V de MVC: pantallas Swing (todavía sin hacer)
├── controlador/          C de MVC: conecta vistas con el modelo (todavía sin hacer)
└── persistencia/         RepositorioPartidas y su implementación en archivo

src/main/resources/imagenes/   Las 52 imágenes del juego (ver su LEEME.md)
src/test/java/            Tests con JUnit 5 (se corren con mvn package)
diagramas/                Diagramas UML (.puml, .png y .svg)
```

Cada paquete tiene un `package-info.java` que lista qué clases del diagrama van ahí.

Las partidas guardadas van a la carpeta `saves/`, que git ignora.

## Estado

El **modelo está completo**: las cuatro capas de los diagramas 1 a 4, unas 90 clases,
con **128 tests de JUnit 5** que pasan. Uno de esos tests juega una partida entera de
la etapa 1 a la 20.

**Este repo es la base del proyecto.** La versión "jugable" que se armó aparte tenía
otro modelo (sin tienda, sin máquina, con 3 enemigos y otros números) y no se usa.
De esa versión se aprovecha la capa visual (controladores, DTOs y pantallas),
que se va pasando a este modelo.

Lo que falta, en orden:

1. Pasar `controlador/`, `dto/` y `vista/` de la versión jugable a este modelo.
2. Diagrama 5 (MVC: controladores, DTOs y vistas).
3. Corregir los diagramas 1, S1 y S2, que nombran métodos que no existen.
4. Renombrar los símbolos de la máquina a los de las imágenes (cereza, limón, etc.).

## Documentación

Los diagramas (`.puml`, `.png` y `.svg`) están en la carpeta [`diagramas/`](diagramas/).

Los números del juego están medidos por simulación y documentados:

| Documento | Qué tiene |
|---|---|
| [CONVENCIONES.md](CONVENCIONES.md) | Cómo escribimos el código. **Leer antes de programar.** |
| [BALANCE_ENEMIGOS.md](BALANCE_ENEMIGOS.md) | Los 11 enemigos: estadísticas, movimientos y escalado por etapa |
| [EQUIPABLES.md](EQUIPABLES.md) | Los 36 equipables, los precios, la economía del oro y el reroll |
| [CONSUMIBLES.md](CONSUMIBLES.md) | Los 5 consumibles y por qué la poción vale lo que vale |
| [MAQUINA_DE_SLOTS.md](MAQUINA_DE_SLOTS.md) | Símbolos, tabla de premios y retorno de la máquina |

Antes de programar, leer [CONVENCIONES.md](CONVENCIONES.md).

## Cómo trabajamos con el repo

- Nadie trabaja sobre una copia suelta: siempre `git pull` antes de empezar.
- Cada tarea en su rama (`git checkout -b tienda-en-la-vista`) y se integra con un pull request.
- Antes de subir: `mvn test` tiene que pasar.
- Si cambia una firma, el diagrama se actualiza en el mismo commit. La consigna exige que coincidan.

## Créditos de las imágenes

Todo CC0. Personajes, enemigos y fondos: *16x16 DungeonTileset II* de 0x72 (el Asesino
y el Curador, con los colores cambiados). Íconos: *496 pixel art icons for medieval/fantasy
RPG* de Henrique Lazarini (7Soul1), en OpenGameArt. La máquina de slots es un dibujo propio.
