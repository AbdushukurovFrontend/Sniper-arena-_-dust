# O'yin tugadi — hamma spawn (lobby)ga qaytadi
execute at @a[tag=sa.ingame] run kill @e[type=minecraft:item,distance=..12]
execute as @a[tag=sa.ingame] run function sniper_arena:lobby/send
tag @a remove sa.winner
kill @e[type=minecraft:armor_stand,tag=sa.cam]
scoreboard players reset * sa.kills
scoreboard players set #state sa.var 0
scoreboard players set #solo sa.var 0
bossbar set sniper_arena:game players
function sniper_arena:game/clear_barrels
tellraw @a[tag=!sa.builder] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Yangi o'yin uchun o'rtadagi doiraga turing!","color":"aqua","bold":false}]
