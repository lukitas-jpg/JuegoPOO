# Licencias y créditos

Qué recursos usa el juego, de dónde salieron y bajo qué licencia. Todo lo que no es
código del grupo es **CC0 (dominio público)**: se puede usar, modificar y redistribuir
sin pedir permiso y sin atribución obligatoria. Igual damos crédito porque corresponde.

## Imágenes de terceros

| Qué | Dónde está | Origen | Autor | Licencia |
|---|---|---|---|---|
| Personajes, enemigos y fondos | `imagenes/personajes/`, `imagenes/enemigos/`, `imagenes/fondos/` | [16x16 DungeonTileset II](https://0x72.itch.io/dungeontileset-ii) | Robert (0x72) | [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/) |
| Íconos de estados, consumibles, símbolos de la máquina y equipables genéricos (`arma_<rareza>.png`, `armadura_<rareza>.png`) | `imagenes/estados/`, `imagenes/consumibles/`, `imagenes/slots/`, `imagenes/equipables/` | [496 pixel art icons for medieval/fantasy RPG](https://opengameart.org/node/39455) | Henrique Lazarini (7Soul1) | [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/) (el autor también lo publicó como dominio público) |

Cambios que hicimos sobre esos recursos:

- Los fondos se armaron combinando baldosas del tileset de 0x72.
- El Asesino y el Curador son sprites del mismo pack con los colores cambiados.
- Todas las imágenes se escalaron sin suavizar a los tamaños que usa el juego (ver `src/main/resources/imagenes/LEEME.md`).

## Dibujos propios del grupo

| Qué | Dónde está | Licencia |
|---|---|---|
| Máquina de slots (`maquina.png`, `maquina_palanca_abajo.png`) | `imagenes/slots/` | CC0 1.0 |
| Dibujo propio de cada uno de los 36 equipables (`arco_corto_de_tejo.png`, etc.) | `imagenes/equipables/` | CC0 1.0 |
| Ícono de Protección (`proteccion.png`) | `imagenes/estados/` | CC0 1.0 |

Los equipables y el ícono de Protección se dibujaron desde cero en el estilo de los íconos
de 7Soul1; no copian ninguno de sus íconos. Se generan con `herramientas/dibujar_equipables.py`.
Los liberamos como CC0 para que todas las imágenes del juego tengan la misma licencia.

## Código

El código Java, los tests, los diagramas y los documentos son trabajo del Grupo 3 para el TPO
de Paradigma Orientado a Objetos (UADE, 2026): Máximo Zaragoza, Santiago González, Giovanni
Oriolani y Lucas Florentin. No tienen una licencia de código abierto.

## Dependencias

| Qué | Para qué | Licencia |
|---|---|---|
| [JUnit 5](https://junit.org/junit5/) (`junit-jupiter`) | Solo para correr los tests; no va dentro del juego | [EPL 2.0](https://www.eclipse.org/legal/epl-2.0/) |
| Java SE (Swing incluido) | Interfaz gráfica | La del JDK que se use (por ejemplo OpenJDK, GPL v2 con Classpath Exception) |
