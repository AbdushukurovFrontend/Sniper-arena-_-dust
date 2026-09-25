# O'yin tugadi — hamma spawn (lobby)ga qaytadi
execute as @a[tag=sa.ingame] run function sniper_arena:lobby/send
tag @a remove sa.winner
kill @e[type=minecraft:marker,tag=sa.cam]
scoreboard players reset * sa.kills
scoreboard players set #state sa.var 0
scoreboard players set #solo sa.var 0
bossbar set sniper_arena:game players
kill @e[type=minecraft:item,x=148,y=-64,z=-89,dx=69,dy=44,dz=95]
function sniper_arena:game/clear_barrels
tellraw @a[tag=!sa.builder] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Yangi o'yin uchun o'rtadagi doiraga turing!","color":"aqua","bold":false}]
