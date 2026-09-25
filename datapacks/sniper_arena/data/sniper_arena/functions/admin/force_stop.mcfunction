scoreboard players operation #tmp sa.var = #state sa.var
execute if score #tmp sa.var matches 2.. run function sniper_arena:game/abort
execute if score #tmp sa.var matches 1 run function sniper_arena:lobby/countdown_cancel
execute if score #tmp sa.var matches ..0 run tellraw @s {"text":"Hozir o'yin ketmayapti.","color":"gray"}
