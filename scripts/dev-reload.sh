#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_PID=""
LAST_FINGERPRINT=""

cleanup() {
    if [[ -n "${APP_PID}" ]] && kill -0 "${APP_PID}" 2>/dev/null; then
        kill "${APP_PID}" 2>/dev/null || true
        wait "${APP_PID}" 2>/dev/null || true
    fi
}

fingerprint() {
    find "${ROOT_DIR}" \
        -path "${ROOT_DIR}/.git" -prune -o \
        -path "${ROOT_DIR}/.gradle" -prune -o \
        -path "${ROOT_DIR}/build" -prune -o \
        -path "${ROOT_DIR}/app/build" -prune -o \
        -path "${ROOT_DIR}/app/bin" -prune -o \
        -type f \
        -printf '%p %T@ %s\n' \
        | sort
}

start_app() {
    cleanup
    echo "[dev-reload] starting app..."
    (
        cd "${ROOT_DIR}"
        ./gradlew run
    ) &
    APP_PID="$!"
}

trap cleanup EXIT INT TERM

LAST_FINGERPRINT="$(fingerprint)"
start_app

while true; do
    sleep 1
    CURRENT_FINGERPRINT="$(fingerprint)"

    if [[ "${CURRENT_FINGERPRINT}" != "${LAST_FINGERPRINT}" ]]; then
        LAST_FINGERPRINT="${CURRENT_FINGERPRINT}"
        echo "[dev-reload] file change detected, restarting app..."
        start_app
    fi
done
