scoreboard players remove #countdown sa.var 1
execute if score #countdown sa.var matches 1.. run function sniper_arena:lobby/countdown_show
execute if score #countdown sa.var matches ..0 run function sniper_arena:game/start
