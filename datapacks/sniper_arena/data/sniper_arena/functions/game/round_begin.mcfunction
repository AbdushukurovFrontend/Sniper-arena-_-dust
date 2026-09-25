# Yangi tur boshlanadi (birinchi marta yoki g'alabadan keyin): doirada turgan yangi o'yinchilar ham
# qo'shiladi, hamma sa.ingame o'yinchi qayta ishga tushiriladi (kill 0, yangi qurol) va arena tanlash
# (ruletka) boshlanadi — mod arena tanlaguncha yoki #choose_ticks o'tguncha (game/choose_fallback)
tag @a[tag=sa.onpad] add sa.ingame
tag @a remove sa.onpad
tag @a remove sa.winner
kill @e[type=minecraft:armor_stand,tag=sa.cam]
scoreboard players add #match_id sa.var 1
scoreboard players set #state sa.var 4
scoreboard players set #choose_t sa.var 0
scoreboard players set #arena_ready sa.var 0
scoreboard players operation #time_left sa.var = #time_limit sa.cfg
scoreboard players reset * sa.kills
execute store result score #alive sa.var if entity @a[tag=sa.ingame]
execute if score #alive sa.var matches ..1 run scoreboard players set #solo sa.var 1
execute if score #alive sa.var matches 2.. run scoreboard players set #solo sa.var 0

function sniper_arena:game/clear_barrels
execute as @a[tag=sa.ingame] run function sniper_arena:game/join_player

title @a[tag=sa.ingame] times 0 110 10
title @a[tag=sa.ingame] subtitle {"text":"Arena tanlanmoqda...","color":"yellow"}
title @a[tag=sa.ingame] title {"text":" "}
execute as @a[tag=sa.ingame] at @s run playsound minecraft:item.goat_horn.sound.0 master @s ~ ~ ~ 1 1
function sniper_arena:game/bossbar
