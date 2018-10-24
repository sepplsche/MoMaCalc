@ECHO OFF

cd C:\dev\git-repos\MoMaCalc\target

SET /P rounds=Rounds: || SET rounds=33
SET /P tiretypes=Tire Types: || SET tiretypes=usss
SET /P tirewears=Tire Wears: || SET tirewears=7 11 16 8 15 16

SET args=-r %rounds% -tc 12 -tt %tiretypes% -tw %tirewears%
>moma.args echo %args%

java -cp MoMaCalc-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.seppl.momacalc.Main %args%

pause

for /f "tokens=* delims=" %%x in (moma.args) do SET args=%%x
echo args from file: %args%

pause
