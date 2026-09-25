# G'olib e'lon qilingan: bir oz kutib hammani lobbyga qaytarish
execute at @a[tag=sa.winner] run particle minecraft:totem_of_undying ~ ~1 ~ 0.4 0.6 0.4 0.15 2
scoreboard players remove #end_timer sa.var 1
execute if score #end_timer sa.var matches ..0 run function sniper_arena:game/finish
