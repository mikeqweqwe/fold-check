#!/usr/bin/env bash
# 在 CI 上用 librime 跑注音簡拼測試：對 cases.txt 每組按鍵印出候選
set -uo pipefail

SRC="$(cd "$(dirname "$0")" && pwd)"
WORK=$(mktemp -d)
cp "$SRC/user/"* "$WORK/"
cd "$WORK"

echo "== rime_deployer 建置方案 =="
rime_deployer --build . || { echo "deployer failed"; exit 1; }
echo "-- build 產物 --"
ls build 2>/dev/null || true

CONSOLE=$(command -v rime_api_console || command -v rime_console || true)
if [ -z "$CONSOLE" ]; then
  echo "找不到 rime console 工具，已安裝檔案清單："
  dpkg -L librime-bin 2>/dev/null || true
  exit 1
fi
echo "console: $CONSOLE"

echo
echo "== 逐案測試 =="
grep -v '^#' "$SRC/cases.txt" | while IFS=$'\t' read -r keys expect note; do
  [ -z "$keys" ] && continue
  echo
  echo "==== 按鍵: $keys | 期待: $expect | $note ===="
  printf '%s\n' "$keys" | "$CONSOLE" 2>&1 | sed -n '1,40p'
done
