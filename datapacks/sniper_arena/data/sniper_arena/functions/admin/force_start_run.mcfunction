tag @a remove sa.onpad
execute at @e[type=minecraft:marker,tag=sa.pad,limit=1] positioned ~ ~0.5 ~ run tag @a[tag=!sa.ingame,tag=!sa.builder,distance=..3.8] add sa.onpad
execute store result score #pad sa.var if entity @a[tag=sa.onpad]
execute if score #pad sa.var matches 0 run tellraw @s {"text":"Doirada hech kim yo'q! Avval doiraga turing (builder rejimi o'chiq bo'lsin).","color":"red"}
execute if score #pad sa.var matches 1.. unless entity @e[type=minecraft:marker,tag=sa.spawn] run tellraw @s {"text":"Arenada spawn nuqtalari yo'q! /function sniper_arena:admin/reset_positions","color":"red"}
execute if score #pad sa.var matches 1.. if entity @e[type=minecraft:marker,tag=sa.spawn] run function sniper_arena:game/start
