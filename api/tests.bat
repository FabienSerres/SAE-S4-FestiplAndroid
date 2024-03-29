@echo off

cls
rmdir Tests\TestPhpUnit\Rapport /s /q
composer exec phpunit -- --testdox-html Tests\TestPhpUnit\Rapport\rapport_tests.html --coverage-html Tests\TestPhpUnit\Rapport\Coverage Tests\TestPhpUnit
pause