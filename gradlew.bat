@echo off
setlocal
set APP_HOME=%~dp0
set GRADLE_VERSION=8.7
set DIST_DIR=%USERPROFILE%\.nova-gradle\%GRADLE_VERSION%
set GRADLE_HOME=%DIST_DIR%\gradle-%GRADLE_VERSION%

if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  echo NOVA Gradle bootstrap requires Gradle %GRADLE_VERSION%.
  echo For the mobile workflow, use GitHub Actions.
  exit /b 1
)

call "%GRADLE_HOME%\bin\gradle.bat" -p "%APP_HOME%" %*
endlocal
