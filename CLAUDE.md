# CLAUDE.md – Tablut KI-Projekt (Meilenstein 3)

## Projektübersicht
Java-basierte Tablut-KI für das TU Berlin KI-Praktikum.
Wettbewerb: 07.06.2026. Immer auf Deutsch antworten.

## Projektstruktur
```
src/main/java/tablut/
├── ki/
│   ├── SearchMoves.java      ← HAUPTDATEI: Alpha-Beta, hier PVS implementieren
│   └── Config.java
├── game/
│   ├── GameLogic.java        ← Spielregeln, Zugausführung, isGameOver
│   ├── Bewertungsfunktion.java ← Bewertungsfunktion (weiter verbessern)
│   ├── MoveFactory.java      ← Zuggenerierung (für Zugsortierung relevant)
│   └── Perft.java            ← Benchmark-Tool
├── board/
│   ├── Board.java            ← Spielzustand, board.copy() für Zugsimulation
│   └── Move.java             ← Zug-Repräsentation (fromX, fromY, toX, toY)
└── client/
    ├── GameClient.java       ← Netzwerk-Client für Spielserver
    └── FenParser.java
```

## Aktueller Stand
- Alpha-Beta-Suche ist implementiert in `SearchMoves.alphaBeta()`
- Iterative Tiefensuche in `findBestMoveAlphaBeta()` (maxDepth=4)
- Zeitmanagement vorhanden (deadline-basiert, Zeitcheck alle 4K Knoten)
- `nodes`-Counter vorhanden für Benchmarking
- MiniMax-Referenzimplementierung noch vorhanden (auskommentiert)

## Meilenstein 3: Aufgaben
**Ziel: Principal Variation Search (PVS) implementieren**

PVS = NegaScout. Kern-Idee:
- Ersten Zug (vermutlich bester) mit vollem Fenster [alpha, beta] durchsuchen
- Alle weiteren Züge nur mit Nullfenster [alpha, alpha+1] durchsuchen (schneller)
- Wenn Nullfenster einen Wert > alpha liefert (fail-high): erneute vollständige Suche

PVS-Pseudocode aus Vorlesung:
```
function pvs(node, depth, α, β, color):
  if depth == 0 or terminal: return color × eval(node)
  for each child:
    if child is first child:
      score = -pvs(child, depth-1, -β, -α, -color)
    else:
      score = -pvs(child, depth-1, -α-1, -α, -color)  // Nullfenster
      if α < score < β:  // fail-high → re-search
        score = -pvs(child, depth-1, -β, -α, -color)
    α = max(α, score)
    if α >= β: break  // beta cutoff
  return α
```

**Wichtig laut Vorlesung:** PVS ist nur effektiv mit guter Zugsortierung!
Daher als weniger komplexe Technik auch Zugsortierung implementieren (z.B. Schlagzüge zuerst).

## Tablut-spezifische Hinweise
- Weiß = Verteidiger (König + Türme), Ziel: König auf Eckfeld
- Schwarz = Angreifer, Ziel: König schlagen
- `board.playBlackTurn == true` → Schwarz ist am Zug
- König-Schlagen: auf Thron von 4 Seiten, neben Thron von 3, sonst normal einschließen
- Eckfelder und Thron nicht betretbar (außer König darf Eckfeld betreten um zu gewinnen)
- Thron darf übersprungen werden wenn leer
- Symmetrien in Tablut nutzbar (für spätere Transposition Table)

## Gameserver
Der lokale Gameserver liegt unter `C:\Users\sechs\Gameserver25`.
Zum Starten in das Verzeichnis wechseln und den Server dort ausführen.
Der Client verbindet sich auf `localhost:5000` (siehe `Main.java` und `GameClient.java`).

## Referenz-Dokumente
Alle PDFs und Unterlagen liegen in: `~/Downloads/KI_Unterlagen/`
Diesen Ordner nur als Referenz verwenden, NICHT bearbeiten.

- `~/Downloads/KI_Unterlagen/21_PJ_KI_fortschrittliche_KI-Techniken.pdf` → PVS Pseudocode Seite 20, Zugsortierung Seiten 22-23
- `~/Downloads/KI_Unterlagen/02_Tablut-Spielregeln.pdf` → Spielregeln

## Coding-Regeln
- KEIN Multithreading
- Keine neuen externen Bibliotheken
- `Board.copy()` für Zugsimulation verwenden (wie bisher)
- Benchmarks mit `nodes`-Counter und Zeitmessung dokumentieren
- Alle Änderungen in `SearchMoves.java`, neue Hilfsmethoden erlaubt
- Bewertungsfunktion in `Bewertungsfunktion.java` separat halten und weiter verbessern