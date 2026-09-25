# Soniyada bir marta ishlaydi
scoreboard players set #tick sa.var 0

# O'yindan tashqaridagi killar hisobga olinmaydi
scoreboard players set @a[tag=!sa.ingame] sa.kill_raw 0

function sniper_arena:lobby/second
execute if score #state sa.var matches 2 run function sniper_arena:game/second
execute if score #show_spawns sa.var matches 1 run function sniper_arena:admin/show_spawns_tick
