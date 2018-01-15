@echo off
set /p PID=<application.pid
taskkill /F /PID %PID%
