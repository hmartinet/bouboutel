@echo off
setlocal

cd /d "%~dp0"

powershell.exe -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ws = New-Object -ComObject WScript.Shell; " ^
  "$desktop = [Environment]::GetFolderPath('Desktop'); " ^
  "$shortcut = $ws.CreateShortcut((Join-Path $desktop 'Bouboutel.lnk')); " ^
  "$shortcut.TargetPath = (Join-Path (Get-Location).Path 'Bouboutel.bat'); " ^
  "$shortcut.WorkingDirectory = (Get-Location).Path; " ^
  "$shortcut.IconLocation = (Join-Path (Get-Location).Path 'icon.ico'); " ^
  "$shortcut.Save()"

if errorlevel 1 (
    echo Failed to create desktop shortcut.
    pause
    exit /b 1
)

echo Desktop shortcut created.
pause
