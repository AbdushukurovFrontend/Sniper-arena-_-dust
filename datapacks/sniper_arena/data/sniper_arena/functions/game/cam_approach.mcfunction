# Kamera qotilga 0.5 blokdan yaqinlashadi: qotilgacha 4.5 blok qolguncha yoki oldinda blok chiqquncha
scoreboard players set #moved sa.var 0
execute unless entity @a[tag=sa.kcam,distance=..4.5] facing entity @a[tag=sa.kcam,limit=1] eyes positioned ^ ^ ^0.5 if block ~ ~ ~ #sniper_arena:cam_clear if block ^ ^ ^0.5 #sniper_arena:cam_clear store success score #moved sa.var run tp @s ~ ~ ~
scoreboard players add #steps sa.var 1
execute if score #moved sa.var matches 1 if score #steps sa.var matches ..300 at @s run function sniper_arena:game/cam_approach
