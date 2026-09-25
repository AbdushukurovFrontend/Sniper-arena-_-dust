# O'yinchini lobbyga qaytaradi va to'liq tozalaydi
tag @s remove sa.ingame
tag @s remove sa.onpad
tag @s remove sa.winner
tag @s remove sa.deathcam
team join sa.lobby @s
gamemode adventure @s
clear @s
effect clear @s
effect give @s minecraft:saturation infinite 0 true
effect give @s minecraft:resistance infinite 4 true
effect give @s minecraft:instant_health 1 4 true
scoreboard players reset @s sa.kills
scoreboard players set @s sa.kill_raw 0
scoreboard players set @s sa.jump 0
scoreboard players set @s sa.dead 0
scoreboard players set @s sa.killer 0
execute if entity @e[type=minecraft:marker,tag=sa.lobby] run tp @s @e[type=minecraft:marker,tag=sa.lobby,limit=1]
execute unless entity @e[type=minecraft:marker,tag=sa.lobby] run tp @s 115.5 -58 -60.5 0 0
execute at @s run spawnpoint @s ~ ~ ~ ~
