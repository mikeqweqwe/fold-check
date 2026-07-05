/* 極簡 librime 探針：從 stdin 讀 cases（按鍵\t期待\t說明），印出前兩頁候選 */
#include <stdio.h>
#include <string.h>
#include <rime_api.h>

static void print_menu(RimeSessionId s) {
  RIME_STRUCT(RimeContext, ctx);
  if (!RimeGetContext(s, &ctx)) { printf("  (無 context)\n"); return; }
  if (ctx.composition.preedit)
    printf("  組字區: %s\n", ctx.composition.preedit);
  if (ctx.menu.num_candidates == 0)
    printf("  (無候選)\n");
  for (int i = 0; i < ctx.menu.num_candidates; ++i)
    printf("  %d. %s\n", ctx.menu.page_no * 9 + i + 1, ctx.menu.candidates[i].text);
  RimeFreeContext(&ctx);
}

int main(int argc, char** argv) {
  if (argc < 3) { fprintf(stderr, "usage: probe <user_dir> <schema_id>\n"); return 1; }
  const char* schema = argv[2];
  RIME_STRUCT(RimeTraits, traits);
  traits.shared_data_dir = "/usr/share/rime-data";
  traits.user_data_dir = argv[1];
  traits.distribution_name = "probe";
  traits.distribution_code_name = "probe";
  traits.distribution_version = "1.0";
  traits.app_name = "rime.probe";
  RimeSetup(&traits);
  RimeInitialize(NULL);
  if (RimeStartMaintenance(True)) RimeJoinMaintenanceThread();

  RimeSessionId s = RimeCreateSession();
  if (!RimeSelectSchema(s, schema)) {
    fprintf(stderr, "無法選 %s 方案\n", schema);
    return 1;
  }

  char line[512];
  while (fgets(line, sizeof line, stdin)) {
    char* nl = strchr(line, '\n');
    if (nl) *nl = 0;
    if (!line[0] || line[0] == '#') continue;
    char* rest = strchr(line, '\t');
    if (rest) *rest++ = 0;
    printf("\n==== 按鍵 %s | %s\n", line, rest ? rest : "");
    RimeProcessKey(s, 0xff1b, 0);
    if (!RimeSimulateKeySequence(s, line)) {
      printf("  (按鍵序列無效)\n");
      continue;
    }
    print_menu(s);
    if (RimeProcessKey(s, 0xff56, 0)) {
      printf("  -- 第二頁 --\n");
      print_menu(s);
    }
  }
  RimeDestroySession(s);
  RimeFinalize();
  return 0;
}
