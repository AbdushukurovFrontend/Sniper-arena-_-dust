# Arenadagi tasodifiy spawn nuqtasiga (iloji bo'lsa, 14 blok ichida boshqa o'yinchi yo'q joyga)
tag @s add sa.self
function sniper_arena:game/spawns_tag
execute as @e[type=minecraft:marker,tag=sa.cur] at @s unless entity @a[tag=sa.ingame,tag=!sa.self,tag=!sa.deathcam,distance=..14] run tag @s add sa.safe
execute if entity @e[type=minecraft:marker,tag=sa.safe] run tp @s @e[type=minecraft:marker,tag=sa.safe,sort=random,limit=1]
execute unless entity @e[type=minecraft:marker,tag=sa.safe] run tp @s @e[type=minecraft:marker,tag=sa.cur,sort=random,limit=1]
tag @e[type=minecraft:marker,tag=sa.safe] remove sa.safe
tag @e[type=minecraft:marker,tag=sa.cur] remove sa.cur
tag @s remove sa.self
