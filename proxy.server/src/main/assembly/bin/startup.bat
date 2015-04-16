
REM check JAVA_HOME & java
set "JAVA_CMD=%JAVA_HOME%/bin/java"
if "%JAVA_HOME%" == "" goto noJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto mainEntry
:noJavaHome
echo ---------------------------------------------------
echo WARN: JAVA_HOME environment variable is not set. 
echo ---------------------------------------------------
set "JAVA_CMD=java"
:mainEntry
REM set HOME_DIR
set "CURR_DIR=%cd%"
cd ..
set "DBPROXY_HOME=%cd%"
cd %CURR_DIR%
"%JAVA_CMD%" -server -Xms1G -Xmx2G -XX:MaxPermSize=64M  -XX:+AggressiveOpts -XX:MaxDirectMemorySize=1G -DDBPROXY_HOME=%DBPROXY_HOME% -cp "..\conf;..\lib\*" com.ctg.udal.ddl.dbproxy.DBProxyStartup