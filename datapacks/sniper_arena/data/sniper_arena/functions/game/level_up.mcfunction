# Har kill => qo'ldagi snayper keyingisiga almashadi
function sniper_arena:game/kit
execute at @s run playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1 1.2
title @s actionbar [{"text":"★ KILL! ","color":"gold","bold":true},{"score":{"name":"@s","objective":"sa.kills"},"color":"yellow","bold":true},{"text":" / ","color":"gray","bold":false},{"score":{"name":"#kills_to_win","objective":"sa.cfg"},"color":"yellow","bold":false},{"text":"   Yangi qurol!","color":"green","bold":false}]
scoreboard players operation #tmp sa.var = #kills_to_win sa.cfg
scoreboard players remove #tmp sa.var 1
execute if score @s sa.kills = #tmp sa.var run tellraw @a[tag=sa.ingame] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"selector":"@s","bold":false},{"text":" g'alabaga 1 ta kill qoldi!","color":"red","bold":false}]
