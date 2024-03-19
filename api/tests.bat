@echo off

cls

for /r Tests\TestPhpUnit %%f in (*.php) do (
    composer exec phpunit "%%f"
    echo -------------------------------------
)

pause