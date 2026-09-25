# O'yinni boshlash: doiradagi hamma o'yinchi arenaga
scoreboard players add #match_id sa.var 1
scoreboard players set #state sa.var 2
scoreboard players operation #time_left sa.var = #time_limit sa.cfg
scoreboard players reset * sa.kills
tag @a[tag=sa.onpad] add sa.ingame
tag @a remove sa.onpad
# 1 kishilik test o'yini (admin/force_start yoki #min_players 1) darhol tugab qolmasin
execute store result score #alive sa.var if entity @a[tag=sa.ingame]
execute if score #alive sa.var matches ..1 run scoreboard players set #solo sa.var 1

function sniper_arena:game/clear_barrels
kill @e[type=minecraft:item,x=148,y=-64,z=-89,dx=69,dy=44,dz=95]
execute as @a[tag=sa.ingame] run function sniper_arena:game/join_player

title @a[tag=sa.ingame] times 5 50 15
title @a[tag=sa.ingame] subtitle [{"text":"Birinchi bo'lib ","color":"yellow"},{"score":{"name":"#kills_to_win","objective":"sa.cfg"},"color":"gold","bold":true},{"text":" ta kill qilgan g'olib!","color":"yellow","bold":false}]
title @a[tag=sa.ingame] title {"text":"⚔ JANG! ⚔","color":"red","bold":true}
execute as @a[tag=sa.ingame] at @s run playsound minecraft:item.goat_horn.sound.0 master @s ~ ~ ~ 1 1
tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"O'yin boshlandi: ","color":"yellow","bold":false},{"selector":"@a[tag=sa.ingame]","bold":false}]
function sniper_arena:game/bossbar
