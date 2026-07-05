#!/usr/bin/env bash
# 在 CI 上用 librime 跑注音簡拼測試：對 cases.txt 每組按鍵印出候選
set -uo pipefail

SRC="$(cd "$(dirname "$0")" && pwd)"
WORK=$(mktemp -d)
cp "$SRC/user/"* "$WORK/"
printf 'patch:\n  schema_list:\n    - schema: bopomofo_tw\n  "menu/page_size": 9\n' > "$WORK/default.custom.yaml"

echo "== 編譯探針 =="
gcc "$SRC/probe.c" -o "$WORK/probe" $(pkg-config --cflags --libs rime 2>/dev/null || echo -lrime) || exit 1

echo "== 建置方案 =="
rime_deployer --build "$WORK" /usr/share/rime-data 2>&1 | tail -5

echo "== 逐案測試 =="
"$WORK/probe" "$WORK" < "$SRC/cases.txt"
