# Kill soniga qarab qurol beradi (1-qo'l slotiga).
# 0 kill -> level_1, 1 kill -> level_2, ... 4 kill -> level_5
execute if score @s sa.kills matches ..0 run function sniper_arena:guns/level_1
execute if score @s sa.kills matches 1 run function sniper_arena:guns/level_2
execute if score @s sa.kills matches 2 run function sniper_arena:guns/level_3
execute if score @s sa.kills matches 3 run function sniper_arena:guns/level_4
execute if score @s sa.kills matches 4.. run function sniper_arena:guns/level_5
