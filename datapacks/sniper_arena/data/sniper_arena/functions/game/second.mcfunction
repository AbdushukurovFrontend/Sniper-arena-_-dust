# O'yin: soniyada bir marta (faqat holat 2)

# Arenada nechta o'yinchi qoldi? 1 ta qolsa — u g'olib
execute store result score #alive sa.var if entity @a[tag=sa.ingame]
execute if score #alive sa.var matches ..1 unless score #solo sa.var matches 1 run function sniper_arena:game/not_enough
execute if score #state sa.var matches 2 if score #alive sa.var matches ..0 run function sniper_arena:game/abort

# Vaqt limiti
execute if score #state sa.var matches 2 if score #time_limit sa.cfg matches 1.. run function sniper_arena:game/timer

# Arena chegarasidan chiqib ketganlar qaytariladi
execute if score #state sa.var matches 2 as @e[type=minecraft:player,tag=sa.ingame,tag=!sa.deathcam] unless entity @s[x=148,y=-64,z=-89,dx=69,dy=44,dz=95] run function sniper_arena:game/out_of_bounds

function sniper_arena:game/clear_barrels

# Jon sekin tiklanadi
scoreboard players add #regen sa.var 1
execute if score #regen sa.var >= #regen_every sa.cfg run function sniper_arena:game/regen
execute if score #state sa.var matches 2 run function sniper_arena:game/bossbar
