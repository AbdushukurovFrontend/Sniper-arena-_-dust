# Doirada yetarli o'yinchi yig'ildi.
# Mod bor bo'lsa arenalarni mod beradi (/sa arena ...); mod bo'lmasa eski usul: arenada spawn markerlari kerak
execute if score #mod sa.var matches 1 run function sniper_arena:lobby/countdown_begin
execute unless score #mod sa.var matches 1 if entity @e[type=minecraft:marker,tag=sa.spawn] run function sniper_arena:lobby/countdown_begin
execute unless score #mod sa.var matches 1 unless entity @e[type=minecraft:marker,tag=sa.spawn] run tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"XATO: arenada spawn nuqtalari yo'q! Admin: /function sniper_arena:admin/reset_positions","color":"red","bold":false}]
