# Yolg'iz test: o'zingizga 1 ta kill yozadi — qurol almashishi va 5 killda g'alabani ikkinchi o'yinchisiz tekshirish uchun
execute if entity @s[tag=sa.ingame] if score #state sa.var matches 2 run scoreboard players add @s sa.kill_raw 1
execute unless entity @s[tag=sa.ingame] run tellraw @s {"text":"Avval o'yinga kiring: doiraga turib /function sniper_arena:admin/force_start","color":"red"}
