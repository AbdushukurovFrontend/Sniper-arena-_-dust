# Vaqt tugadi: eng ko'p kill qilgan yolg'iz o'yinchi g'olib, teng bo'lsa durang
scoreboard players set #max sa.var 0
scoreboard players operation #max sa.var > @a[tag=sa.ingame] sa.kills
scoreboard players set #top sa.var 0
execute as @a[tag=sa.ingame] if score @s sa.kills = #max sa.var run scoreboard players add #top sa.var 1
scoreboard players set #reason sa.var 3
execute if score #max sa.var matches 1.. if score #top sa.var matches 1 as @a[tag=sa.ingame] if score @s sa.kills = #max sa.var run function sniper_arena:game/win
execute if score #state sa.var matches 2 run function sniper_arena:game/draw
