# Lobby (kirishdagi paydo bo'lish) joyini shu yerga ko'chirish. Yerda turib, kerakli tomonga qarab ishlating.
execute unless entity @e[type=minecraft:marker,tag=sa.lobby] at @s run summon minecraft:marker ~ ~ ~ {Tags:["sa.marker","sa.lobby"]}
tp @e[type=minecraft:marker,tag=sa.lobby] @s
execute at @s run forceload add ~ ~
execute at @s run setworldspawn ~ ~ ~ ~
execute at @s as @a[tag=!sa.ingame,tag=!sa.builder] run spawnpoint @s ~ ~ ~ ~
scoreboard players set #markers sa.var 1
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Lobby joyi o'rnatildi.","color":"green","bold":false}]
