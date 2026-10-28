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

## Dibujo propio de cada equipable (opcional)

Cada uno de los 36 equipables puede tener su propio dibujo en `equipables/`. Si el
archivo existe, la tienda lo usa; si no, usa el genérico de su rareza
(`arma_raro.png`, `armadura_epico.png`, etc.). Se pueden ir agregando de a uno.

Mismo tamaño que el resto de los íconos (68×68, o 34×34: el juego los lleva a 68×68
sin suavizar). El nombre del archivo es el nombre del objeto en minúscula, sin tildes
y con guiones bajos:

| Clase | Lugar | Rareza | Objeto | Archivo |
|---|---|---|---|---|
| Arquero | Arma | Raro | Arco Corto de Tejo | `arco_corto_de_tejo.png` |
| Arquero | Arma | Epico | Arco Largo del Vendaval | `arco_largo_del_vendaval.png` |
| Arquero | Arma | Legendario | Arco de la Luna Menguante | `arco_de_la_luna_menguante.png` |
| Arquero | Armadura | Raro | Jubon de Cuero Endurecido | `jubon_de_cuero_endurecido.png` |
| Arquero | Armadura | Epico | Atuendo del Montaraz | `atuendo_del_montaraz.png` |
| Arquero | Armadura | Legendario | Vestimenta del Cazador Eterno | `vestimenta_del_cazador_eterno.png` |
| Asesino | Arma | Raro | Daga de Hoja Negra | `daga_de_hoja_negra.png` |
| Asesino | Arma | Epico | Colmillo Gemelo | `colmillo_gemelo.png` |
| Asesino | Arma | Legendario | Estilete de la Ultima Sombra | `estilete_de_la_ultima_sombra.png` |
| Asesino | Armadura | Raro | Traje de Contrabandista | `traje_de_contrabandista.png` |
| Asesino | Armadura | Epico | Sayo del Gremio Silencioso | `sayo_del_gremio_silencioso.png` |
| Asesino | Armadura | Legendario | Sudario del Sin Nombre | `sudario_del_sin_nombre.png` |
| Curador | Arma | Raro | Cetro de Plata Bendecida | `cetro_de_plata_bendecida.png` |
| Curador | Arma | Epico | Baculo del Alba | `baculo_del_alba.png` |
| Curador | Arma | Legendario | Cetro de la Luz Primera | `cetro_de_la_luz_primera.png` |
| Curador | Armadura | Raro | Habito de Lino Sagrado | `habito_de_lino_sagrado.png` |
| Curador | Armadura | Epico | Vestiduras del Santuario | `vestiduras_del_santuario.png` |
| Curador | Armadura | Legendario | Manto de la Aurora | `manto_de_la_aurora.png` |
| Guerrero | Arma | Raro | Espada de Acero Templado | `espada_de_acero_templado.png` |
| Guerrero | Arma | Epico | Mandoble del Juramento | `mandoble_del_juramento.png` |
| Guerrero | Arma | Legendario | Filo del Primer Amanecer | `filo_del_primer_amanecer.png` |
| Guerrero | Armadura | Raro | Cota de Malla Reforzada | `cota_de_malla_reforzada.png` |
| Guerrero | Armadura | Epico | Armadura de Placas del Bastion | `armadura_de_placas_del_bastion.png` |
| Guerrero | Armadura | Legendario | Egida del Rey Caido | `egida_del_rey_caido.png` |
| Mago | Arma | Raro | Baculo de Roble Runico | `baculo_de_roble_runico.png` |
| Mago | Arma | Epico | Cetro de Cuarzo Ardiente | `cetro_de_cuarzo_ardiente.png` |
| Mago | Arma | Legendario | Vara del Eclipse | `vara_del_eclipse.png` |
| Mago | Armadura | Raro | Tunica de Hilo Runico | `tunica_de_hilo_runico.png` |
| Mago | Armadura | Epico | Manto del Conclave | `manto_del_conclave.png` |
| Mago | Armadura | Legendario | Vestiduras del Vacio | `vestiduras_del_vacio.png` |
| Tanque | Arma | Raro | Maza de Hierro Macizo | `maza_de_hierro_macizo.png` |
| Tanque | Arma | Epico | Martillo del Yunque | `martillo_del_yunque.png` |
| Tanque | Arma | Legendario | Quebrantamontanas | `quebrantamontanas.png` |
| Tanque | Armadura | Raro | Armadura de Escamas Pesada | `armadura_de_escamas_pesada.png` |
| Tanque | Armadura | Epico | Coraza del Muro Inquebrantable | `coraza_del_muro_inquebrantable.png` |
| Tanque | Armadura | Legendario | Baluarte de Obsidiana | `baluarte_de_obsidiana.png` |

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

Los 36 dibujos propios de los equipables (`arco_corto_de_tejo.png`, etc.) y el ícono `estados/proteccion.png` también se dibujaron aparte, en el estilo de los íconos de 7Soul1: son originales del grupo y los liberamos como CC0. Se pueden rehacer o cambiar con `herramientas/dibujar_equipables.py`; el color del detalle marca la rareza (azul = raro, violeta = épico, dorado = legendario).

El detalle completo de licencias, con links, está en `LICENCIAS.md`, en la raíz del repo.
