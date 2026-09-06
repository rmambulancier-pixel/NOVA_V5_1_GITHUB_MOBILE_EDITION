#!/bin/sh
#
# NOVA Gradle bootstrap for GitHub Actions and Unix-like environments.
#
# This lightweight bootstrap downloads Gradle 8.7 when the standard wrapper
# JAR is not bundled, making the repository mobile-friendly.
#

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.9
DIST_DIR="${HOME}/.nova-gradle/${GRADLE_VERSION}"
GRADLE_HOME="${DIST_DIR}/gradle-${GRADLE_VERSION}"

if [ ! -x "${GRADLE_HOME}/bin/gradle" ]; then
  mkdir -p "${DIST_DIR}"
  ARCHIVE="${DIST_DIR}/gradle-${GRADLE_VERSION}-bin.zip"
  if [ ! -f "${ARCHIVE}" ]; then
    echo "Downloading Gradle ${GRADLE_VERSION}..."
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "${ARCHIVE}"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O "${ARCHIVE}"
    else
      echo "Neither curl nor wget is available." >&2
      exit 1
    fi
  fi
  echo "Installing Gradle ${GRADLE_VERSION}..."
  rm -rf "${GRADLE_HOME}"
  unzip -q "${ARCHIVE}" -d "${DIST_DIR}"
fi

exec "${GRADLE_HOME}/bin/gradle" -p "${APP_HOME}" "$@"
