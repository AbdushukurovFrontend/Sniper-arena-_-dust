# Har #regen_every soniyada tirik o'yinchilarga +4 jon (instant_health 0-daraja = 4 jon)
scoreboard players set #regen sa.var 0
effect give @e[type=minecraft:player,tag=sa.ingame,tag=!sa.deathcam] minecraft:instant_health 1 0 true
