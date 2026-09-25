# Qotil 3.5 blokdan yaqin bo'lsa kamera orqaga chekinadi (orqada blok bo'lmasa), qotil to'liq ko'rinsin
scoreboard players set #moved sa.var 0
execute if entity @a[tag=sa.kcam,distance=..3.5] facing entity @a[tag=sa.kcam,limit=1] eyes positioned ^ ^ ^-0.5 if block ~ ~ ~ #sniper_arena:cam_clear if block ^ ^ ^-0.5 #sniper_arena:cam_clear store success score #moved sa.var run tp @s ~ ~ ~
scoreboard players add #steps sa.var 1
execute if score #moved sa.var matches 1 if score #steps sa.var matches ..8 at @s run function sniper_arena:game/cam_back
