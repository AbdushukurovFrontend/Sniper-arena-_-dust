# Doirada yetarli o'yinchi yig'ildi
execute if entity @e[type=minecraft:marker,tag=sa.spawn] run function sniper_arena:lobby/countdown_begin
execute unless entity @e[type=minecraft:marker,tag=sa.spawn] run tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"XATO: arenada spawn nuqtalari yo'q! Admin: /function sniper_arena:admin/reset_positions","color":"red","bold":false}]
