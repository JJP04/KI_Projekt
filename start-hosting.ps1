# start-hosting.ps1
# Startet Gameserver + bore-Tunnel jeweils in einem eigenen Fenster.
# Aufruf (im KI_Projekt-Ordner):
#     .\start-hosting.ps1
# Optional festen oeffentlichen Port erzwingen:
#     .\start-hosting.ps1 -RemotePort 23675

param(
    [int]$LocalPort  = 5000,
    [int]$RemotePort = 0   # 0 = bore vergibt zufaellig; sonst fester Port
)

$serverDir = "C:\Users\finnf\Downloads\Gameserver25-main"

Write-Host "Starte Gameserver auf Port $LocalPort ..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$serverDir'; uv run python -m gameserver --port $LocalPort -vv"
)

Start-Sleep -Seconds 2

if ($RemotePort -gt 0) {
    $boreArgs = "local $LocalPort --to bore.pub --port $RemotePort"
} else {
    $boreArgs = "local $LocalPort --to bore.pub"
}

Write-Host "Starte bore-Tunnel ($boreArgs) ..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "bore $boreArgs"
)

Write-Host ""
Write-Host "Zwei Fenster geoeffnet: Gameserver + bore-Tunnel." -ForegroundColor Green
Write-Host "Im bore-Fenster steht 'listening at bore.pub:XXXXX'." -ForegroundColor Green
Write-Host "Diese Zahl in Main.java (Zeile 21) als Port eintragen." -ForegroundColor Green
