# Shu o'yin arenasining spawn markerlari (mod ularga sa.match = #match_id beradi) -> sa.cur
# Mod bo'lmasa (eski usul) sa.match yo'q — unda hamma sa.spawn markerlari
tag @e[type=minecraft:marker,tag=sa.cur] remove sa.cur
execute as @e[type=minecraft:marker,tag=sa.spawn] if score @s sa.match = #match_id sa.var run tag @s add sa.cur
execute unless entity @e[type=minecraft:marker,tag=sa.cur] run tag @e[type=minecraft:marker,tag=sa.spawn] add sa.cur
