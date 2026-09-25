# O'lim ekrani: 3 soniya kuzatuvchi (spectator) rejimida, kamera qotilga qaragan holda qulflanadi
tag @s add sa.deathcam
scoreboard players set @s sa.dead 60
gamemode spectator @s
clear @s
effect clear @s

# Qotilni topish (killed_by_player saqlagan ID bo'yicha)
tag @a remove sa.kcam
scoreboard players operation #k sa.var = @s sa.killer
execute if score #k sa.var matches 1.. as @a[tag=sa.ingame] if score @s sa.pid = #k sa.var run tag @s add sa.kcam

# Kamera o'lgan joyda yaratilgan (killed_by_player). Bo'lmasa (qotilsiz o'lim) — shu yerda,
# o'yinchi arenadan uzoqda bo'lsa (masalan pastga yiqilgan) arenaning tasodifiy spawn nuqtasida
scoreboard players operation #me sa.var = @s sa.pid
scoreboard players set #has_cam sa.var 0
execute as @e[type=minecraft:armor_stand,tag=sa.cam] if score @s sa.pid = #me sa.var run scoreboard players set #has_cam sa.var 1
function sniper_arena:game/spawns_tag
execute if score #has_cam sa.var matches 0 at @s if entity @e[type=minecraft:marker,tag=sa.cur,distance=..80] run function sniper_arena:game/cam_create
execute if score #has_cam sa.var matches 0 at @s unless entity @e[type=minecraft:marker,tag=sa.cur,distance=..80] at @e[type=minecraft:marker,tag=sa.cur,sort=random,limit=1] run function sniper_arena:game/cam_create
tag @e[type=minecraft:marker,tag=sa.cur] remove sa.cur

# Kamera qotilga qaratiladi va o'yinchi unga bog'lanadi (/spectate): ko'rinish sichqoncha bilan buzilmaydi
execute as @e[type=minecraft:armor_stand,tag=sa.cam] if score @s sa.pid = #me sa.var run tag @s add sa.mycam
execute if entity @a[tag=sa.kcam] as @e[type=minecraft:armor_stand,tag=sa.mycam] at @s run function sniper_arena:game/cam_aim
spectate @e[type=minecraft:armor_stand,tag=sa.mycam,limit=1] @s
tag @e[type=minecraft:armor_stand,tag=sa.mycam] remove sa.mycam

title @s times 0 60 10
execute if entity @a[tag=sa.kcam] run title @s subtitle [{"text":"Sizni ","color":"gray"},{"selector":"@a[tag=sa.kcam]","color":"red","bold":true},{"text":" o'ldirdi","color":"gray","bold":false}]
execute unless entity @a[tag=sa.kcam] run title @s subtitle {"text":"3 soniyadan keyin qayta tug'ilasiz","color":"gray"}
title @s title {"text":"O'LDIRILDINGIZ","color":"red","bold":true}
tag @a remove sa.kcam
