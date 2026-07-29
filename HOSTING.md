
# Hosting: Gameserver + bore-Tunnel

Kurzanleitung, um ein Spiel zu hosten, dem sich ein Mitspieler über das Internet
verbinden kann. Drei Bausteine laufen parallel:

```
Dein KI-Client ─┐
                ├─▶ Gameserver (lokal, Port 5000) ──▶ bore ──▶ bore.pub:XXXXX ◀── Mitspieler
Mitspieler ─────┘
```

Ordner Gameserver: `C:\Users\finnf\Downloads\Gameserver25-main`

---

## 1. Gameserver starten (Fenster 1)

```powershell
cd C:\Users\finnf\Downloads\Gameserver25-main
uv run python -m gameserver --port 5000 -vv
```

- `--port 5000` = lokaler Port
- `-vv` = zeigt Log-Ausgaben (Verbindungen/Fehler); ohne bleibt der Server still
- NICHT `--no-handshake` / `--no-auth` nutzen — der Java-Client braucht Handshake + Login
- Fenster offen lassen.

## 2. bore-Tunnel öffnen (Fenster 2)

```powershell
bore local 5000 --to bore.pub
```

Ausgabe: `listening at bore.pub:XXXXX` — die Zahl `XXXXX` ist eure öffentliche Adresse.
Fenster offen lassen.

Optional feste Portnummer (falls frei), damit sie nicht bei jedem Start wechselt:

```powershell
bore local 5000 --to bore.pub --port 23675
```

## 3. Verbinden

**Du (Java-Client):**
1. In `src/main/java/tablut/Main.java` Port auf die bore-Zahl setzen:
   ```java
   private static final int port = XXXXX;   // Zeile 21
   ```
2. Programm starten -> `1` (Gameserver) -> `c` (create) -> Lobby-Name (z.B. `F`)
3. Warten, bis der Mitspieler drin ist, dann Enter -> Spiel startet.

**Mitspieler:**
- Java: Programm -> `1` -> `j` (join) -> gleicher Lobby-Name `F`
- oder Python:
  ```powershell
  cd C:\Users\finnf\Downloads\Gameserver25-main
  uv run python -m gameclient --host bore.pub --port XXXXX
  ```

---

## Test, ob der Server lokal lauscht (Fenster 3, optional)

```powershell
cd C:\Users\finnf\Downloads\Gameserver25-main
uv run python -m gameclient --host localhost --port 5000
```

Verbindet sich das, steht die Kette. Danach Fenster 3 wieder schließen.

## Tipps

- Reihenfolge: erst Server (1), dann Tunnel (2), dann Clients (3).
- Server- und Tunnel-Fenster müssen beide offen bleiben, solange gespielt wird.
- `gameclient`/`gameserver` gibt es nur im Ordner `Gameserver25-main` — von dort starten.
