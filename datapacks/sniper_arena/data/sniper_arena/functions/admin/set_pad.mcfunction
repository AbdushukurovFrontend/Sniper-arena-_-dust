# O'yin boshlash doirasi markazini shu yerga ko'chirish (radius 3.8 blok)
execute unless entity @e[type=minecraft:marker,tag=sa.pad] at @s run summon minecraft:marker ~ ~ ~ {Tags:["sa.marker","sa.pad"]}
tp @e[type=minecraft:marker,tag=sa.pad] @s
execute at @s run tp @e[type=minecraft:text_display,tag=sa.holo] ~ ~3.2 ~
execute at @s run forceload add ~ ~
scoreboard players set #markers sa.var 1
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Doira markazi o'rnatildi.","color":"green","bold":false}]
