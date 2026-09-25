# O'yinchini lobbyga qaytaradi va to'liq tozalaydi
tag @s remove sa.ingame
tag @s remove sa.onpad
tag @s remove sa.winner
tag @s remove sa.deathcam
team join sa.lobby @s
gamemode adventure @s
clear @s
effect clear @s
attribute @s minecraft:generic.max_health base set 100
attribute @s minecraft:generic.attack_damage modifier remove 5a0c1e2d-0000-4000-8000-00000000a001
effect give @s minecraft:saturation infinite 0 true
effect give @s minecraft:resistance infinite 4 true
effect give @s minecraft:instant_health 1 5 true
scoreboard players reset @s sa.kills
scoreboard players set @s sa.kill_raw 0
scoreboard players set @s sa.jump 0
scoreboard players set @s sa.dead 0
scoreboard players set @s sa.killer 0
scoreboard players operation #me sa.var = @s sa.pid
execute as @e[type=minecraft:armor_stand,tag=sa.cam] if score @s sa.pid = #me sa.var run kill @s
execute if entity @e[type=minecraft:marker,tag=sa.lobby] run tp @s @e[type=minecraft:marker,tag=sa.lobby,limit=1]
execute unless entity @e[type=minecraft:marker,tag=sa.lobby] run tp @s 115.5 -58 -60.5 0 0
execute at @s run spawnpoint @s ~ ~ ~ ~
