# ============================================================
#  SNIPER ARENA — SOZLAMALAR
#  Qiymatni o'zgartirgach o'yinda /reload yozing.
# ============================================================

# G'alaba uchun kerakli kill soni.
# Har bir kill uchun alohida qurol bor: guns/level_1 ... guns/level_18
scoreboard players set #kills_to_win sa.cfg 18

# O'yin boshlanishi uchun doirada turishi kerak bo'lgan eng kam o'yinchi soni
scoreboard players set #min_players sa.cfg 2

# Doirada yetarli o'yinchi bo'lgach, boshlanishgacha sanoq (soniya)
scoreboard players set #countdown sa.cfg 10

# O'yin vaqt limiti (soniya). Vaqt tugasa eng ko'p kill qilgan g'olib bo'ladi.
# 0 = vaqt limiti yo'q
scoreboard players set #time_limit sa.cfg 900

# O'yinda jon sekin tiklanadi: har necha soniyada +4 jon (100 jondan)
scoreboard players set #regen_every sa.cfg 5

# G'olib e'lon qilingandan keyin lobbyga qaytishgacha kutish (tick, 20 tick = 1 soniya)
scoreboard players set #end_delay sa.cfg 120

# Ichki doimiy qiymat (o'zgartirmang)
scoreboard players set #60 sa.var 60
