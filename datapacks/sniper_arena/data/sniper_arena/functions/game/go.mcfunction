# Arena tanlandi, ruletka tugadi: jang boshlanadi
scoreboard players set #state sa.var 2
execute as @a[tag=sa.ingame] run function sniper_arena:game/go_player
title @a[tag=sa.ingame] times 5 50 15
title @a[tag=sa.ingame] subtitle [{"text":"Birinchi bo'lib ","color":"yellow"},{"score":{"name":"#kills_to_win","objective":"sa.cfg"},"color":"gold","bold":true},{"text":" ta kill qilgan g'olib!","color":"yellow","bold":false}]
title @a[tag=sa.ingame] title {"text":"⚔ JANG! ⚔","color":"red","bold":true}
execute as @a[tag=sa.ingame] at @s run playsound minecraft:item.goat_horn.sound.0 master @s ~ ~ ~ 1 1
tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"O'yin boshlandi: ","color":"yellow","bold":false},{"selector":"@a[tag=sa.ingame]","bold":false}]
function sniper_arena:game/bossbar
