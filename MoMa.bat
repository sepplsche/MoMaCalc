@ECHO OFF

SET /P rounds=Rounds: || SET rounds=33
SET /P tiretypes=Tire Types: || SET tiretypes=usss
SET /P tirewears=Tire Wears: || SET tirewears=7 11 16 8 15 16

cd C:\dev\git-repos\MoMaCalc\target
java -cp MoMaCalc-0.0.1-SNAPSHOT-jar-with-dependencies.jar de.seppl.momacalc.Main -r %rounds% -tc 12 -tt %tiretypes% -tw %tirewears%

pause
