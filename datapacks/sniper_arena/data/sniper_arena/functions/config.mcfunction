# ============================================================
#  SNIPER ARENA — SOZLAMALAR
#  Qiymatni o'zgartirgach o'yinda /reload yozing.
# ============================================================

# G'alaba uchun kerakli kill soni.
# Har bir kill uchun alohida qurol bor: guns/level_1 ... guns/level_15
scoreboard players set #kills_to_win sa.cfg 15

# O'yin boshlanishi uchun doirada turishi kerak bo'lgan eng kam o'yinchi soni
scoreboard players set #min_players sa.cfg 2

# Doirada yetarli o'yinchi bo'lgach, boshlanishgacha sanoq (soniya)
scoreboard players set #countdown sa.cfg 10

# Arena tanlash (ruletka oynasi) davomiyligi, tick. Mod oynasi 4.9 s: 1 s kutish + 3 s aylanish + to'xtash
scoreboard players set #choose_ticks sa.cfg 100

# O'yin vaqt limiti (soniya). Vaqt tugasa eng ko'p kill qilgan g'olib bo'ladi.
# 0 = vaqt limiti yo'q
scoreboard players set #time_limit sa.cfg 900

# O'yinda jon sekin tiklanadi: har necha soniyada +4 jon (100 jondan)
scoreboard players set #regen_every sa.cfg 5

# G'olib e'lon qilingandan keyin lobbyga qaytishgacha kutish (tick, 20 tick = 1 soniya)
scoreboard players set #end_delay sa.cfg 120

# Arenadagi o'lim respawnsiz ("virtual o'lim", Sniper Arena HUD modi kerak — hostda ham).
# 1.20.1 da har respawnda server o'yinchiga atrofdagi hamma chunklarni qayta yuboradi: internet orqali
# kirgan o'yinchida ping ko'tariladi va u chiqib ketishi mumkin. 1 = yoqilgan (tavsiya), 0 = oddiy o'lim
scoreboard players set #virtual_death sa.cfg 1

# Ichki doimiy qiymat (o'zgartirmang)
scoreboard players set #60 sa.var 60
