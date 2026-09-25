# O'yindagilar uchun panel: qolgan vaqt va g'alaba sharti
bossbar set sniper_arena:game players @a[tag=sa.ingame]
# HUD mod uchun: qolgan vaqt va g'alaba sharti (# bilan boshlangan nomlar sidebar'da ko'rinmaydi)
scoreboard players operation #time sa.kills = #time_left sa.var
scoreboard players operation #target sa.kills = #kills_to_win sa.cfg
execute if score #time_limit sa.cfg matches 1.. run function sniper_arena:game/bossbar_timer
execute unless score #time_limit sa.cfg matches 1.. run function sniper_arena:game/bossbar_notimer
