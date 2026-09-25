# O'lim ekrani: 3 soniya kuzatuvchi (spectator) rejimida, kamera qotilga qaragan holda
tag @s add sa.deathcam
scoreboard players set @s sa.dead 60
gamemode spectator @s
clear @s

# Qotilni topish (killed_by_player saqlagan ID bo'yicha)
tag @a remove sa.kcam
scoreboard players operation #k sa.var = @s sa.killer
execute if score #k sa.var matches 1.. as @a[tag=sa.ingame] if score @s sa.pid = #k sa.var run tag @s add sa.kcam

# Kamera joyi: qotil qaragan tomonda, 3.5 blok oldida va biroz tepada (qotilning yuzi ko'rinadi)
execute if entity @a[tag=sa.kcam] at @a[tag=sa.kcam,limit=1] rotated ~ 0 run summon minecraft:marker ^ ^1 ^3.5 {Tags:["sa.cam","sa.newcam"]}
execute unless entity @a[tag=sa.kcam] at @s run summon minecraft:marker ~ ~2 ~ {Tags:["sa.cam","sa.newcam"]}
scoreboard players operation @e[type=minecraft:marker,tag=sa.newcam] sa.pid = @s sa.pid
tag @e[type=minecraft:marker,tag=sa.newcam] remove sa.newcam

title @s times 0 60 10
execute if entity @a[tag=sa.kcam] run title @s subtitle [{"text":"Sizni ","color":"gray"},{"selector":"@a[tag=sa.kcam]","color":"red","bold":true},{"text":" o'ldirdi","color":"gray","bold":false}]
execute unless entity @a[tag=sa.kcam] run title @s subtitle {"text":"3 soniyadan keyin qayta tug'ilasiz","color":"gray"}
title @s title {"text":"O'LDIRILDINGIZ","color":"red","bold":true}
tag @a remove sa.kcam
