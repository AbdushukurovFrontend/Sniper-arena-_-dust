# Vaqt tugadi va eng ko'p kill teng — durang
scoreboard players set #state sa.var 3
scoreboard players operation #end_timer sa.var = #end_delay sa.cfg
effect give @a[tag=sa.ingame] minecraft:resistance 15 4 true
title @a[tag=sa.ingame] times 5 70 15
title @a[tag=sa.ingame] subtitle {"text":"Vaqt tugadi — g'olib yo'q","color":"gray"}
title @a[tag=sa.ingame] title {"text":"DURANG","color":"yellow","bold":true}
tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"O'yin durang bilan tugadi (vaqt tugadi).","color":"yellow","bold":false}]
execute as @a[tag=sa.ingame] at @s run playsound minecraft:block.note_block.bass master @s ~ ~ ~ 1 0.8
bossbar set sniper_arena:game name {"text":"DURANG","color":"yellow","bold":true}
