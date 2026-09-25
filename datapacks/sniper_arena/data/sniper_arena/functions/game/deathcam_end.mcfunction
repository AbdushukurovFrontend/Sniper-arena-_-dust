# 3 soniya tugadi: arenaning xavfsiz joyida qayta tug'ilish (qurol darajasi saqlanadi)
tag @s remove sa.deathcam
scoreboard players set @s sa.dead 0
scoreboard players set @s sa.killer 0
scoreboard players operation #me sa.var = @s sa.pid
execute as @e[type=minecraft:marker,tag=sa.cam] if score @s sa.pid = #me sa.var run kill @s
title @s clear
gamemode adventure @s
function sniper_arena:game/spawn_random
function sniper_arena:game/kit
function sniper_arena:game/spawn_effects
title @s actionbar {"text":"Qayta tug'ildingiz — 3 soniya himoya","color":"aqua"}
