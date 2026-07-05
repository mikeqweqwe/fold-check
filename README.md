# 折疊偵測驗證 App（fold-check）

這是一個丟棄式的驗證用 App，目標機為 OPPO Find N6（Android 16）。全螢幕即時顯示折疊狀態，用來確認 `androidx.window` 在此機型上能不能正確讀到 FoldingFeature 資料。

顯示內容：
- 折疊狀態：FLAT / HALF_OPENED / 無折疊特徵 / 等待資料中
- 鉸鏈方向、鉸鏈位置（bounds）
- 視窗寬度（px 與 dp）
- 是否視為「展開」（寬度 ≥ 600dp）

開合手機時畫面會即時更新。

## 怎麼從 Release 下載 APK

1. 打開這個 repo 的 GitHub 頁面，點右側 **Releases**。
2. 找到最新一筆（tag 名稱類似 `build-12-1`），展開後下載 `app-debug.apk`。
3. 若沒看到 Releases，改到 **Actions** 分頁，點最新一次成功的 workflow run，在 **Artifacts** 區塊下載 `foldcheck-debug-apk`。

## 實機測試步驟

1. 手機上開啟「允許安裝未知來源 App」，把下載好的 `app-debug.apk` 傳到手機並安裝。
2. 打開 App，畫面應立刻顯示五行資訊；若鉸鏈資料還沒到，「折疊狀態」會顯示「等待資料…」。
3. 實際把手機折起來、展開，觀察畫面是否即時跟著變化（折疊狀態、鉸鏈方向/位置、寬度 px/dp、是否展開）。
