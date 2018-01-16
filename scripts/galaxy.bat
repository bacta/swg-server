@echo off

if "%~1"=="start" GOTO START
if "%~1"=="stop" GOTO STOP

GOTO PARAMS

:START
start java -Dspring.pid.file=galaxy.pid -Dlogging.file=logs/galaxy.log -cp lib\shared\*;lib\galaxy\* io.bacta.galaxy.server.GalaxyServerApplication
GOTO DONE

:STOP
set /p PID=<galaxy.pid
start windows-kill.exe -SIGINT %PID%

:PARAMS
echo "usage: start|stop"

:DONE