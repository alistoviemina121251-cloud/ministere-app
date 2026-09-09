@echo off
setlocal enabledelayedexpansion

set WRAPPER_JAR=".mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar

if exist %WRAPPER_JAR% goto :run

echo Downloading maven-wrapper.jar...
powershell -Command "Invoke-WebRequest -Uri %DOWNLOAD_URL% -OutFile %WRAPPER_JAR%"

:run
"%JAVA_HOME%\bin\java.exe" -jar %WRAPPER_JAR% %*
