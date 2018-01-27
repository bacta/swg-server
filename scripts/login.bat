@echo off

if "%~1"=="start" GOTO START
if "%~1"=="stop" GOTO STOP

GOTO PARAMS

:START
start java -Dspring.pid.file=login.pid -Dlogging.file=logs/login.log -cp lib\shared\*;lib\login\* io.bacta.login.server.LoginServerApplication
GOTO DONE

:STOP
set /p PID=<login.pid
start windows-kill.exe -SIGINT %PID%
GOTO DONE

:PARAMS
echo "usage: start|stop"

:DONE