#!/usr/bin/env bash
# 在 CI 上用 librime 跑注音簡拼測試：官方 bopomofo_tw 與 銥注音 iridium_bpmf 各跑一輪 cases 對比
set -uo pipefail

SRC="$(cd "$(dirname "$0")" && pwd)"

echo "== 編譯探針 =="
PROBE=$(mktemp)
gcc "$SRC/probe.c" -o "$PROBE" $(pkg-config --cflags --libs rime 2>/dev/null || echo -lrime) || exit 1

run_variant() {
  local dir="$1" schema="$2" label="$3" shared="${4:-/usr/share/rime-data}"
  local work
  work=$(mktemp -d)
  cp "$SRC/$dir/"* "$work/"
  echo
  echo "######## 方案：$label ########"
  rime_deployer --build "$work" "$shared" 2>&1 | tail -3
  "$PROBE" "$work" "$schema" < "$SRC/cases.txt"
}

EMPTY_SHARED=$(mktemp -d)

run_variant user bopomofo_tw "官方 bopomofo_tw（essay 詞庫）"
run_variant user-iridium iridium_bpmf "銥注音（McBopomofo 台灣詞庫）"
run_variant user-dafa iridium_bpmf "大發設定（銥注音+空白選字）"
run_variant user-dafa iridium_bpmf "大發-手機模擬（共享目錄全空，等同 APK 實機部署）" "$EMPTY_SHARED"
