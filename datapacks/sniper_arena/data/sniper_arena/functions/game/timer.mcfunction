scoreboard players remove #time_left sa.var 1
execute if score #time_left sa.var matches 60 run tellraw @a[tag=sa.ingame] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"1 daqiqa qoldi!","color":"yellow","bold":false}]
execute if score #time_left sa.var matches 10 run tellraw @a[tag=sa.ingame] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"10 soniya qoldi!","color":"red","bold":false}]
execute if score #time_left sa.var matches ..0 run function sniper_arena:game/timeup
