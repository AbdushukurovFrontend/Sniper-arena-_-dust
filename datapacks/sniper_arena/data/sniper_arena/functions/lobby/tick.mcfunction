# Lobby: har tick
gamemode adventure @a[tag=!sa.ingame,tag=!sa.builder,gamemode=!adventure]
execute as @e[type=minecraft:player,tag=!sa.ingame,tag=!sa.builder,scores={sa.deaths=1..}] run function sniper_arena:lobby/respawned

# O'yinda qatnashmayotgan hech kim arenaga kira olmaydi
execute as @e[type=minecraft:player,tag=!sa.ingame,tag=!sa.builder,x=148,y=-64,z=-89,dx=69,dy=44,dz=95] run function sniper_arena:lobby/send

# Doira atrofida aylanuvchi zarrachalar
execute as @e[type=minecraft:marker,tag=sa.pad,limit=1] at @s run tp @s ~ ~ ~ ~6 0
execute if score #state sa.var matches 0 at @e[type=minecraft:marker,tag=sa.pad,limit=1] run function sniper_arena:lobby/ring_wait
execute if score #state sa.var matches 1 at @e[type=minecraft:marker,tag=sa.pad,limit=1] run function sniper_arena:lobby/ring_countdown
execute if score #state sa.var matches 2.. at @e[type=minecraft:marker,tag=sa.pad,limit=1] run function sniper_arena:lobby/ring_busy
