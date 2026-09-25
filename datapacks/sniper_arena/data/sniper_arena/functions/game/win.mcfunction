# G'olib (@s). #reason: 1 = kill soni, 2 = raqiblar chiqib ketdi, 3 = vaqt tugadi
scoreboard players set #state sa.var 3
scoreboard players operation #end_timer sa.var = #end_delay sa.cfg
scoreboard players add @s sa.wins 1
tag @s add sa.winner
effect give @a[tag=sa.ingame] minecraft:resistance 15 4 true

title @a[tag=sa.ingame] times 5 70 15
execute if score #reason sa.var matches 1 run title @a[tag=sa.ingame] subtitle [{"text":"G'OLIB!  ","color":"yellow","bold":true},{"score":{"name":"@s","objective":"sa.kills"},"color":"gold","bold":false},{"text":" kill","color":"gold","bold":false}]
execute if score #reason sa.var matches 2 run title @a[tag=sa.ingame] subtitle {"text":"G'OLIB! (raqiblar o'yinni tark etdi)","color":"yellow","bold":true}
execute if score #reason sa.var matches 3 run title @a[tag=sa.ingame] subtitle {"text":"G'OLIB! (vaqt tugadi — eng ko'p kill)","color":"yellow","bold":true}
title @a[tag=sa.ingame] title [{"text":"★ ","color":"gold"},{"selector":"@s","color":"gold","bold":true},{"text":" ★","color":"gold"}]
tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"G'olib: ","color":"yellow","bold":false},{"selector":"@s","color":"gold","bold":true},{"text":" (","color":"gray","bold":false},{"score":{"name":"@s","objective":"sa.kills"},"color":"yellow","bold":false},{"text":" kill)","color":"gray","bold":false}]
execute as @a[tag=sa.ingame] at @s run playsound minecraft:ui.toast.challenge_complete master @s ~ ~ ~ 1 1
execute at @s run summon minecraft:firework_rocket ~ ~3 ~ {LifeTime:25,FireworksItem:{id:"minecraft:firework_rocket",Count:1b,tag:{Fireworks:{Flight:2b,Explosions:[{Type:1b,Flicker:1b,Trail:1b,Colors:[I;16766720,16777215],FadeColors:[I;16733525]}]}}}}
bossbar set sniper_arena:game name [{"text":"G'olib: ","color":"gold"},{"selector":"@s","color":"yellow","bold":true}]
