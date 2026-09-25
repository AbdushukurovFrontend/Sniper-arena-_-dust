# O'yindagilar uchun panel: qolgan vaqt va g'alaba sharti
bossbar set sniper_arena:game players @a[tag=sa.ingame]
execute if score #time_limit sa.cfg matches 1.. run function sniper_arena:game/bossbar_timer
execute unless score #time_limit sa.cfg matches 1.. run function sniper_arena:game/bossbar_notimer
