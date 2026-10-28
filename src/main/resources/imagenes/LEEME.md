# Imágenes del juego

52 imágenes. Esta carpeta completa va en la carpeta de recursos del proyecto: `src/main/resources/imagenes/` si usan Maven, o `src/imagenes/` si no.

## Convención de nombres

| Carpeta | Nombre del archivo | Ejemplo |
|---|---|---|
| `personajes/` | nombre de la clase en minúscula | `Guerrero` → `guerrero.png` |
| `enemigos/` | nombre de la clase en minúscula | `SeniorDeLasSombras` → `seniordelassombras.png` |
| `estados/` | nombre de la clase de `EfectoEstado` en minúscula | `BuffEstadistica` → `buffestadistica.png` |
| `consumibles/` | clase de `EfectoConsumible` sin "Efecto", en minúscula | `EfectoRestaurarMana` → `restaurarmana.png` |
| `equipables/` | `slot_rareza` en minúscula | `ARMA` + `EPICO` → `arma_epico.png` |
| `slots/` | valor de `SimboloSlot` en minúscula, más la máquina | `CEREZA` → `cereza.png` |
| `fondos/` | ambiente (ver tabla de abajo) | `cripta.png` |

`estados/defendiendo.png` no corresponde a un `EfectoEstado`: es para mostrar que un combatiente eligió Defender.

Los símbolos de la máquina son CEREZA, LIMON, TREBOL, RUBI, DIAMANTE y MONEDA. Si el enum `SimboloSlot` usa otros nombres, renombrá los archivos o el enum.

## Fondo según la etapa

| Etapas | Fondo |
|---|---|
| 1, 2, 3, 5 | `mazmorra.png` |
| 6, 7, 8 y 10 (minijefe) | `cripta.png` |
| 11, 12, 13, 15 | `cloaca.png` |
| 16, 17, 18 | `infierno.png` |
| 20 (jefe final) | `jefe.png` |
| 4, 9, 14, 19 (tiendas) | `tienda.png` |

La elección del fondo va en la vista, no en el modelo: el modelo no tiene por qué saber cómo se ve una etapa.

## Máquina de slots

- `slots/maquina.png` (412×312) es la máquina en reposo y `slots/maquina_palanca_abajo.png` es la misma con la palanca bajada, para mostrar mientras gira.
- Las ventanas de los rodillos están vacías: el código dibuja encima los símbolos de 68×68, en estas posiciones dentro de la imagen de la máquina:

| Rodillo | x | y |
|---|---|---|
| 1 | 56 | 118 |
| 2 | 152 | 118 |
| 3 | 248 | 118 |

- El resultado lo decide el modelo (`ResultadoTirada`). La vista solo anima los rodillos con un `javax.swing.Timer` y los frena sobre los símbolos que ya vinieron en el resultado.

## Tamaños

Ya vienen escaladas, así que no hace falta escalarlas en el código:

- Personajes: 64×112. Enemigos chicos: 64×112. Enemigos grandes (Bruto, Verdugo, Señor de las Sombras): 128×144.
- Íconos: 68×68.
- Fondos: 960×540.
- Máquina de slots: 412×312.

Los personajes miran a la derecha y los enemigos a la izquierda. Si las escalás más en Swing, usá `RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR` para que el pixel art no quede borroso.

## Licencias

Todo es CC0 (dominio público): se puede usar sin pedir permiso y sin atribución obligatoria. Igual queda bien darles crédito en la pantalla de créditos o en el README del repo:

- Personajes, enemigos y fondos: **0x72 DungeonTileset II** — https://0x72.itch.io/dungeontileset-ii
- Íconos: **496 pixel art icons for medieval/fantasy RPG**, de Henrique Lazarini (7Soul1) — https://opengameart.org/node/39455

Los fondos se armaron combinando baldosas del tileset de 0x72. El Asesino y el Curador son sprites del mismo pack con los colores cambiados. La máquina de slots se dibujó aparte, en el mismo estilo.
