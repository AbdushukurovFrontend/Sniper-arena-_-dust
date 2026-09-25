execute store result bossbar sniper_arena:game max run scoreboard players get #time_limit sa.cfg
execute store result bossbar sniper_arena:game value run scoreboard players get #time_left sa.var
scoreboard players operation #mm sa.var = #time_left sa.var
scoreboard players operation #mm sa.var /= #60 sa.var
scoreboard players operation #ss sa.var = #time_left sa.var
scoreboard players operation #ss sa.var %= #60 sa.var
execute if score #ss sa.var matches 10.. run bossbar set sniper_arena:game name [{"text":"Vaqt: ","color":"gray"},{"score":{"name":"#mm","objective":"sa.var"},"color":"white"},{"text":":","color":"white"},{"score":{"name":"#ss","objective":"sa.var"},"color":"white"},{"text":"    G'alaba: ","color":"gray"},{"score":{"name":"#kills_to_win","objective":"sa.cfg"},"color":"gold"},{"text":" kill","color":"gold"}]
execute if score #ss sa.var matches ..9 run bossbar set sniper_arena:game name [{"text":"Vaqt: ","color":"gray"},{"score":{"name":"#mm","objective":"sa.var"},"color":"white"},{"text":":0","color":"white"},{"score":{"name":"#ss","objective":"sa.var"},"color":"white"},{"text":"    G'alaba: ","color":"gray"},{"score":{"name":"#kills_to_win","objective":"sa.cfg"},"color":"gold"},{"text":" kill","color":"gold"}]
