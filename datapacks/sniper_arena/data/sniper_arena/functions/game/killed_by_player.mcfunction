# O'yinchi boshqa o'yinchi tomonidan o'ldirilgan zahoti (o'lgan o'yinchi nomidan, o'lgan joyida) ishlaydi:
# qotilning ID raqamini eslab qoladi va killcam kamerasini shu joyda yaratadi
advancement revoke @s only sniper_arena:killed_by_player
scoreboard players set #k sa.var 0
execute on attacker run scoreboard players operation #k sa.var = @s sa.pid
scoreboard players operation @s sa.killer = #k sa.var
execute if entity @s[tag=sa.ingame] run function sniper_arena:game/cam_create
execute if entity @s[tag=sa.ingame] run particle minecraft:poof ~ ~1 ~ 0.3 0.5 0.3 0.02 12
