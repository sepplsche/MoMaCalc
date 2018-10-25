@ECHO OFF

cd C:\dev\git-repos\MoMaCalc\target

SET /P rounds=Rounds: || SET rounds=33
SET /P tiretypes=Tire Types: || SET tiretypes=usss
SET /P tirewears=Tire Wears: || SET tirewears=7 11 16 8 15 16

SET args=-r %rounds% -tc 12 -tt %tiretypes% -tw %tirewears%
REM >moma.args echo %args%

java -cp MoMaCalc-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.seppl.momacalc.Main -m S %args%

pause

:loop
REM for /f "tokens=* delims=" %%x in (moma.args) do SET args=%%x

SET /P drivemode=Drive Mode: || SET drivemode=3
SET /P tireusage=Tire Usage: || SET tireusage=5
SET /P tireleft=Tire Left: || SET tireleft=100
echo.
SET /P motormode=Motor Mode: || SET motormode=2
SET /P fuelusage=Fuel Usage: || SET fuelusage=0
SET /P fuelleft=Fuel Left: || SET fuelleft=0

SET args=-r %rounds% -tc 12 -tt %tiretypes% -tw %tirewears% -dm=%drivemode% %tireusage% %tireleft% -mm=%motormode% %fuelusage% %fuelleft%
REM >moma.args echo %args%

java -cp MoMaCalc-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.seppl.momacalc.Main -m R %args%

goto :loop

pause
