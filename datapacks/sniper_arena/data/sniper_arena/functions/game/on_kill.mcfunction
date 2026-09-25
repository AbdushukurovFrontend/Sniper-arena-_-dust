# Kill qilgan o'yinchi (@s)
scoreboard players operation @s sa.kills += @s sa.kill_raw
scoreboard players operation @s sa.tkills += @s sa.kill_raw
scoreboard players set @s sa.kill_raw 0
scoreboard players set #reason sa.var 1
execute if score #state sa.var matches 2 if score @s sa.kills >= #kills_to_win sa.cfg run function sniper_arena:game/win
execute if score #state sa.var matches 2 run function sniper_arena:game/level_up
