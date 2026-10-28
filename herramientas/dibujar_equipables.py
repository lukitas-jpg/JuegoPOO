"""Dibuja los 36 equipables como pixel art de 34x34 (se guardan a 68x68).

No hace falta para compilar ni para jugar: los PNG ya estan en src/main/resources/imagenes/.
Sirve para cambiar o rehacer alguno. Necesita Python 3 y Pillow (pip install pillow):

    python3 herramientas/dibujar_equipables.py salida/

Deja salida/equipables/<objeto>.png. Cada dibujo es
una funcion corta registrada con @dibujo("<nombre_del_archivo>"). El color del detalle
marca la rareza: azul = raro, violeta = epico, dorado con destellos = legendario.

Dibujos originales del grupo (no salen de ningun pack). Licencia: ver LICENCIAS.md.
"""
import math, os, sys
from PIL import Image

N = 34
SALIDA = sys.argv[1]

def ramp(r, g, b):
    base = (r, g, b)
    osc = tuple(int(c * 0.62) for c in base)
    luz = tuple(min(255, int(c * 1.22 + 34)) for c in base)
    return (osc, base, luz)

# Acentos de rareza (igual en todos los objetos de esa rareza)
RARO = ramp(70, 130, 225)
EPICO = ramp(160, 80, 215)
LEGEND = ramp(240, 188, 55)
BRILLO = (255, 250, 220)

class Lienzo:
    def __init__(self):
        self.parte = [[None] * N for _ in range(N)]
        self.ramps = []
        self.planos = []

    def color(self, rmp, plano=False):
        self.ramps.append(rmp)
        self.planos.append(plano)
        return len(self.ramps) - 1

    def px(self, p, x, y):
        x, y = int(round(x)), int(round(y))
        if 0 <= x < N and 0 <= y < N:
            self.parte[y][x] = p

    def borrar(self, x, y):
        if 0 <= x < N and 0 <= y < N:
            self.parte[y][x] = None

    def linea(self, p, x0, y0, x1, y1, w=1):
        pasos = int(max(abs(x1 - x0), abs(y1 - y0)) * 2) + 1
        for i in range(pasos + 1):
            t = i / pasos
            x, y = x0 + (x1 - x0) * t, y0 + (y1 - y0) * t
            self.disco(p, x, y, w / 2.0)

    def disco(self, p, cx, cy, r):
        if r <= 0.5:
            self.px(p, cx, cy)
            return
        for y in range(int(cy - r) - 1, int(cy + r) + 2):
            for x in range(int(cx - r) - 1, int(cx + r) + 2):
                if (x - cx) ** 2 + (y - cy) ** 2 <= r * r:
                    self.px(p, x, y)

    def elipse(self, p, cx, cy, rx, ry):
        for y in range(N):
            for x in range(N):
                if ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2 <= 1.0:
                    self.parte[y][x] = p

    def poligono(self, p, pts):
        for y in range(N):
            for x in range(N):
                if dentro(x + 0.5, y + 0.5, pts):
                    self.parte[y][x] = p

    def rect(self, p, x0, y0, x1, y1):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.px(p, x, y)

    def arco(self, p, cx, cy, rx, ry, a0, a1, w=2, w_medio=None):
        pasos = 80
        for i in range(pasos + 1):
            t = i / pasos
            a = math.radians(a0 + (a1 - a0) * t)
            ancho = w if w_medio is None else w + (w_medio - w) * math.sin(math.pi * t)
            self.disco(p, cx + rx * math.cos(a), cy + ry * math.sin(a), ancho / 2.0)

    def destello(self, x, y):
        p = self.color((BRILLO, BRILLO, BRILLO), plano=True)
        for dx, dy in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
            self.px(p, x + dx, y + dy)

    def render(self):
        img = Image.new("RGBA", (N, N), (0, 0, 0, 0))
        q = img.load()
        g = self.parte
        def de(x, y):
            return g[y][x] if 0 <= x < N and 0 <= y < N else None
        for y in range(N):
            for x in range(N):
                p = g[y][x]
                if p is None:
                    continue
                osc, base, luz = self.ramps[p]
                if self.planos[p]:
                    c = base
                elif de(x, y - 1) != p or de(x - 1, y) != p:
                    c = luz
                elif de(x, y + 1) != p or de(x + 1, y) != p:
                    c = osc
                else:
                    c = base
                q[x, y] = c + (255,)
        for y in range(N):
            for x in range(N):
                if g[y][x] is not None:
                    continue
                vecinos = [de(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))]
                vecinos = [v for v in vecinos if v is not None]
                if vecinos:
                    osc = self.ramps[vecinos[0]][0]
                    q[x, y] = tuple(int(c * 0.32) for c in osc) + (255,)
        return img

def dentro(x, y, pts):
    adentro = False
    j = len(pts) - 1
    for i in range(len(pts)):
        xi, yi = pts[i]
        xj, yj = pts[j]
        if (yi > y) != (yj > y) and x < (xj - xi) * (y - yi) / (yj - yi) + xi:
            adentro = not adentro
        j = i
    return adentro

# ------------------------------------------------------------ piezas reutilizables

def espada(L, hoja, guarda, mango, largo=15, ancho=2, gema=None, punta=(5, 5), guarda_larga=4):
    x0, y0 = punta
    x1, y1 = x0 + largo, y0 + largo
    h = L.color(hoja)
    L.linea(h, x0 + 1, y0 + 1, x1, y1, ancho)
    L.px(h, x0, y0)
    gu = L.color(guarda)
    g = guarda_larga
    L.linea(gu, x1 - g, y1 + g, x1 + g, y1 - g, 2)
    m = L.color(mango)
    L.linea(m, x1 + 2, y1 + 2, x1 + 6, y1 + 6, 2)
    if gema:
        L.disco(L.color(gema), x1 + 7, y1 + 7, 1.5)
    return h

def cabeza(cx, cy, largo, grosor):
    """Cabeza de martillo: rectangulo perpendicular a un mango que sube en diagonal hacia la derecha."""
    u, v = (0.707, 0.707), (0.707, -0.707)
    return [(cx + su * largo * u[0] + sv * grosor * v[0], cy + su * largo * u[1] + sv * grosor * v[1])
            for su, sv in ((1, 1), (1, -1), (-1, -1), (-1, 1))]

def baston(L, madera, desde=(7, 30), hasta=(22, 11), ancho=2):
    m = L.color(madera)
    L.linea(m, desde[0], desde[1], hasta[0], hasta[1], ancho)
    return m

TORSO = [(8, 10), (13, 6), (21, 6), (26, 10), (27, 15), (24, 16), (24, 29), (10, 29), (10, 16), (7, 15)]
TUNICA = [(9, 9), (13, 5), (21, 5), (25, 9), (26, 14), (23, 14), (27, 31), (7, 31), (11, 14), (8, 14)]

def cuello(L, cx=17, cy=6, rx=3, ry=2):
    for y in range(N):
        for x in range(N):
            if ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2 <= 1.0:
                L.borrar(x, y)

def hombreras(L, rmp, r=4):
    p = L.color(rmp)
    L.elipse(p, 8, 11, r, r - 1)
    L.elipse(p, 26, 11, r, r - 1)
    return p

def capucha(L, tela, cara=(30, 25, 35)):
    t = L.color(tela)
    L.poligono(t, TUNICA)
    L.elipse(t, 17, 9, 7, 7)
    c = L.color(ramp(*cara), plano=True)
    L.elipse(c, 17, 10, 3.6, 3.8)
    return t

# ------------------------------------------------------------ los 37 dibujos

DIBUJOS = {}
def dibujo(nombre):
    def registrar(f):
        DIBUJOS[nombre] = f
        return f
    return registrar

# --- Arquero: arcos
def arco(L, madera, cuerda, w, wm, acento, bulto=11):
    m = L.color(madera)
    L.arco(m, 11, 17, bulto, 14, -90, 90, w, wm)
    c = L.color(cuerda, plano=True)
    L.linea(c, 11, 3, 11, 31, 1)
    a = L.color(acento)
    L.rect(a, 10 + bulto, 15, 12 + bulto, 19)

@dibujo("arco_corto_de_tejo")
def _(L):
    arco(L, ramp(150, 95, 50), ramp(225, 220, 200), 2, 3, RARO, bulto=10)

@dibujo("arco_largo_del_vendaval")
def _(L):
    arco(L, ramp(105, 175, 185), ramp(235, 240, 245), 2, 3, EPICO, bulto=12)
    v = L.color(ramp(200, 235, 245), plano=True)
    for y0 in (8, 24):
        L.linea(v, 27, y0, 31, y0 - 1)
        L.linea(v, 28, y0 + 2, 32, y0 + 2)

@dibujo("arco_de_la_luna_menguante")
def _(L):
    arco(L, LEGEND, ramp(230, 235, 250), 1, 5, ramp(245, 245, 255), bulto=12)
    L.destello(28, 5)
    L.destello(5, 27)

# --- Asesino: dagas
@dibujo("daga_de_hoja_negra")
def _(L):
    espada(L, ramp(70, 70, 88), RARO, ramp(110, 70, 40), largo=11, ancho=3, punta=(8, 8), guarda_larga=2)

@dibujo("colmillo_gemelo")
def _(L):
    hueso = ramp(225, 218, 195)
    h = L.color(hueso)
    L.linea(h, 5, 5, 17, 17, 2)
    L.linea(h, 28, 5, 16, 17, 2)
    g = L.color(EPICO)
    L.linea(g, 14, 22, 22, 14, 2)
    L.linea(g, 11, 14, 19, 22, 2)
    m = L.color(ramp(60, 40, 70))
    L.linea(m, 20, 20, 25, 25, 2)
    L.linea(m, 13, 20, 8, 25, 2)

@dibujo("estilete_de_la_ultima_sombra")
def _(L):
    espada(L, ramp(95, 55, 130), LEGEND, ramp(40, 30, 50), largo=16, ancho=1, punta=(4, 4), guarda_larga=2)
    filo = L.color(ramp(200, 150, 255), plano=True)
    for i in range(6, 18, 3):
        L.px(filo, i + 1, i)
    L.destello(26, 6)

# --- Curador: cetros y baculos
@dibujo("cetro_de_plata_bendecida")
def _(L):
    baston(L, ramp(190, 195, 210), desde=(9, 29), hasta=(20, 15))
    o = L.color(RARO)
    L.disco(o, 23, 11, 4.5)
    L.linea(L.color(ramp(190, 195, 210)), 17, 19, 21, 23, 2)

@dibujo("baculo_del_alba")
def _(L):
    baston(L, ramp(235, 225, 205), desde=(6, 31), hasta=(21, 12))
    s = L.color(ramp(250, 160, 90))
    for ang in range(0, 360, 45):
        a = math.radians(ang)
        L.linea(s, 24, 9, 24 + 7 * math.cos(a), 9 + 7 * math.sin(a), 1)
    L.disco(L.color(ramp(255, 205, 120)), 24, 9, 4)
    L.disco(L.color(EPICO), 15, 20, 1.5)

@dibujo("cetro_de_la_luz_primera")
def _(L):
    baston(L, LEGEND, desde=(8, 30), hasta=(20, 16))
    e = L.color(ramp(255, 245, 200))
    L.poligono(e, [(23, 2), (25, 9), (32, 11), (25, 13), (23, 20), (21, 13), (14, 11), (21, 9)])
    L.destello(7, 6)
    L.destello(29, 27)

# --- Guerrero: espadas
@dibujo("espada_de_acero_templado")
def _(L):
    espada(L, ramp(175, 185, 200), RARO, ramp(115, 75, 45), gema=RARO)

@dibujo("mandoble_del_juramento")
def _(L):
    espada(L, ramp(200, 205, 220), EPICO, ramp(70, 45, 90), largo=17, ancho=3, punta=(3, 3), gema=EPICO)

@dibujo("filo_del_primer_amanecer")
def _(L):
    espada(L, ramp(255, 215, 150), LEGEND, ramp(150, 60, 40), largo=16, ancho=3, punta=(4, 4), gema=ramp(240, 80, 60))
    L.destello(5, 15)
    L.destello(15, 4)

# --- Mago: baculos y varas
@dibujo("baculo_de_roble_runico")
def _(L):
    m = baston(L, ramp(125, 85, 50), desde=(6, 31), hasta=(21, 12), ancho=3)
    L.arco(m, 24, 9, 4, 4, 90, 380, 2)
    L.disco(L.color(RARO), 24, 9, 1.6)

@dibujo("cetro_de_cuarzo_ardiente")
def _(L):
    baston(L, ramp(70, 45, 35), desde=(7, 31), hasta=(20, 14), ancho=2)
    L.linea(L.color(EPICO), 17, 19, 21, 15, 2)
    L.poligono(L.color(ramp(240, 90, 50)), [(25, 2), (30, 8), (25, 17), (20, 9)])

@dibujo("vara_del_eclipse")
def _(L):
    baston(L, ramp(45, 40, 60), desde=(7, 31), hasta=(19, 15), ancho=2)
    L.disco(L.color(LEGEND), 23, 10, 7)
    L.disco(L.color(ramp(25, 20, 40), plano=True), 23, 10, 4.6)
    L.destello(9, 6)

# --- Tanque: mazas y martillos
@dibujo("maza_de_hierro_macizo")
def _(L):
    baston(L, ramp(120, 80, 45), desde=(7, 29), hasta=(19, 17), ancho=3)
    L.linea(L.color(RARO), 16, 18, 19, 21, 2)
    h = L.color(ramp(140, 145, 155))
    L.disco(h, 22, 13, 6)
    for dx, dy in ((0, -8), (8, 0), (6, -6), (-6, -6), (6, 6)):
        L.linea(h, 22, 13, 22 + dx, 13 + dy, 2)

@dibujo("martillo_del_yunque")
def _(L):
    baston(L, ramp(100, 70, 45), desde=(6, 31), hasta=(19, 16), ancho=3)
    L.poligono(L.color(ramp(150, 155, 170)), cabeza(21, 13, 8, 4))
    r = L.color(EPICO, plano=True)
    L.linea(r, 17, 10, 24, 17)

@dibujo("quebrantamontanas")
def _(L):
    baston(L, ramp(90, 60, 40), desde=(5, 32), hasta=(18, 18), ancho=3)
    L.poligono(L.color(ramp(120, 110, 100)), cabeza(20, 13, 11, 6))
    L.poligono(L.color(LEGEND), cabeza(20, 13, 7, 3))
    L.destello(5, 6)
    L.destello(30, 27)

# --- Arquero: armaduras
@dibujo("jubon_de_cuero_endurecido")
def _(L):
    L.poligono(L.color(ramp(150, 100, 60)), TORSO)
    cuello(L)
    c = L.color(ramp(80, 50, 30), plano=True)
    for y in range(10, 28, 3):
        L.linea(c, 15, y, 19, y + 2)
        L.linea(c, 19, y, 15, y + 2)
    L.rect(L.color(RARO), 15, 22, 19, 23)

@dibujo("atuendo_del_montaraz")
def _(L):
    capucha(L, ramp(70, 120, 60))
    L.disco(L.color(EPICO), 17, 17, 1.6)

@dibujo("vestimenta_del_cazador_eterno")
def _(L):
    L.poligono(L.color(ramp(60, 115, 55)), TORSO)
    cuello(L)
    o = L.color(LEGEND)
    L.linea(o, 10, 16, 10, 29, 1)
    L.linea(o, 24, 16, 24, 29, 1)
    L.poligono(o, [(17, 13), (21, 18), (17, 25), (13, 18)])
    L.destello(28, 4)

# --- Asesino: armaduras
@dibujo("traje_de_contrabandista")
def _(L):
    L.poligono(L.color(ramp(75, 75, 85)), TORSO)
    cuello(L)
    L.linea(L.color(ramp(120, 80, 45)), 10, 12, 23, 26, 2)
    L.rect(L.color(RARO), 15, 17, 18, 20)

@dibujo("sayo_del_gremio_silencioso")
def _(L):
    capucha(L, ramp(85, 50, 110), cara=(20, 15, 25))
    L.linea(L.color(EPICO), 13, 17, 21, 17, 2)

@dibujo("sudario_del_sin_nombre")
def _(L):
    t = capucha(L, ramp(45, 42, 55), cara=(8, 6, 12))
    for x in range(8, 27, 4):
        L.borrar(x, 31)
        L.borrar(x + 1, 31)
        L.borrar(x, 30)
    ojo = L.color(LEGEND, plano=True)
    L.px(ojo, 15, 10)
    L.px(ojo, 19, 10)
    L.destello(29, 4)

# --- Curador: armaduras
@dibujo("habito_de_lino_sagrado")
def _(L):
    L.poligono(L.color(ramp(225, 220, 200)), TUNICA)
    cuello(L, cy=5)
    c = L.color(RARO)
    L.rect(c, 16, 10, 18, 22)
    L.rect(c, 13, 13, 21, 15)

@dibujo("vestiduras_del_santuario")
def _(L):
    L.poligono(L.color(ramp(235, 232, 220)), TUNICA)
    cuello(L, cy=5)
    o = L.color(ramp(225, 180, 70))
    L.linea(o, 8, 30, 26, 30, 2)
    s = L.color(EPICO)
    L.linea(s, 14, 7, 12, 28, 2)
    L.linea(s, 20, 7, 22, 28, 2)

@dibujo("manto_de_la_aurora")
def _(L):
    L.poligono(L.color(ramp(240, 140, 120)), TUNICA)
    cuello(L, cy=5)
    L.poligono(L.color(ramp(250, 180, 100)), [(9, 22), (25, 22), (27, 31), (7, 31)])
    o = L.color(LEGEND)
    L.disco(o, 17, 15, 3.5)
    L.destello(28, 5)

# --- Guerrero: armaduras
@dibujo("cota_de_malla_reforzada")
def _(L):
    p = L.color(ramp(150, 155, 165))
    L.poligono(p, TORSO)
    cuello(L)
    m = L.color(ramp(100, 105, 115), plano=True)
    for y in range(9, 29, 2):
        for x in range(9 + (y // 2) % 2, 26, 2):
            if L.parte[y][x] == p:
                L.px(m, x, y)
    L.linea(L.color(RARO), 13, 8, 21, 8, 2)

@dibujo("armadura_de_placas_del_bastion")
def _(L):
    p = L.color(ramp(130, 150, 185))
    L.poligono(p, TORSO)
    cuello(L)
    hombreras(L, ramp(150, 170, 200))
    l = L.color(ramp(80, 95, 125), plano=True)
    for y in (17, 22, 26):
        L.linea(l, 11, y, 23, y)
    L.disco(L.color(EPICO), 17, 12, 1.6)

@dibujo("egida_del_rey_caido")
def _(L):
    L.poligono(L.color(LEGEND), TORSO)
    cuello(L)
    hombreras(L, ramp(220, 165, 50))
    c = L.color(ramp(200, 60, 60))
    L.poligono(c, [(13, 18), (13, 13), (15, 15), (17, 12), (19, 15), (21, 13), (21, 18)])
    L.destello(29, 3)

# --- Mago: armaduras
@dibujo("tunica_de_hilo_runico")
def _(L):
    L.poligono(L.color(ramp(60, 90, 170)), TUNICA)
    cuello(L, cy=5)
    r = L.color(ramp(150, 190, 250), plano=True)
    for (x, y) in ((13, 14), (20, 18), (15, 24), (21, 27), (11, 28)):
        L.linea(r, x, y, x + 2, y)
        L.px(r, x + 1, y - 1)
    L.linea(L.color(RARO), 16, 6, 18, 6, 1)

@dibujo("manto_del_conclave")
def _(L):
    L.poligono(L.color(ramp(110, 55, 150)), TUNICA)
    L.poligono(L.color(ramp(140, 80, 180)), [(10, 9), (13, 2), (21, 2), (24, 9), (17, 12)])
    cuello(L, cy=6)
    e = L.color(ramp(240, 210, 110), plano=True)
    for (x, y) in ((12, 17), (21, 20), (16, 25), (23, 28), (10, 28)):
        L.px(e, x, y)

@dibujo("vestiduras_del_vacio")
def _(L):
    L.poligono(L.color(ramp(35, 30, 70)), TUNICA)
    cuello(L, cy=5)
    s = L.color((BRILLO, BRILLO, BRILLO), plano=True)
    for (x, y) in ((12, 13), (19, 16), (15, 21), (22, 24), (11, 27), (18, 28)):
        L.px(s, x, y)
    L.linea(L.color(LEGEND), 8, 30, 26, 30, 2)
    L.destello(29, 4)

# --- Tanque: armaduras
@dibujo("armadura_de_escamas_pesada")
def _(L):
    p = L.color(ramp(110, 135, 115))
    L.poligono(p, TORSO)
    cuello(L)
    hombreras(L, ramp(120, 145, 125))
    e = L.color(ramp(70, 90, 75), plano=True)
    for y in range(12, 29, 3):
        for x in range(10 + (y % 2) * 2, 25, 4):
            if L.parte[y][x] == p:
                L.px(e, x, y)
                L.px(e, x + 1, y + 1)
                L.px(e, x - 1, y + 1)
    L.rect(L.color(RARO), 16, 9, 18, 10)

@dibujo("coraza_del_muro_inquebrantable")
def _(L):
    L.poligono(L.color(ramp(140, 135, 130)), TORSO)
    cuello(L)
    hombreras(L, ramp(160, 155, 150), r=5)
    l = L.color(ramp(90, 85, 82), plano=True)
    L.linea(l, 17, 9, 17, 28)
    v = L.color(EPICO)
    for (x, y) in ((13, 14), (21, 14), (13, 24), (21, 24)):
        L.disco(v, x, y, 1.2)

@dibujo("baluarte_de_obsidiana")
def _(L):
    L.poligono(L.color(ramp(45, 35, 60)), TORSO)
    cuello(L)
    hombreras(L, ramp(60, 45, 80), r=5)
    b = L.color(ramp(150, 100, 220), plano=True)
    L.linea(b, 12, 13, 15, 20)
    L.linea(b, 21, 13, 19, 18)
    L.linea(L.color(LEGEND), 11, 28, 23, 28, 2)
    L.destello(30, 3)

# ------------------------------------------------------------ salida
os.makedirs(os.path.join(SALIDA, "equipables"), exist_ok=True)
for nombre, f in DIBUJOS.items():
    L = Lienzo()
    f(L)
    img = L.render().resize((68, 68), Image.NEAREST)
    img.save(os.path.join(SALIDA, "equipables", nombre + ".png"))
print(len(DIBUJOS), "dibujos")
