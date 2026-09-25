# O'yin: har tick (holat 2 = o'yin, 3 = g'olib e'lon qilingan)

# Killar (faqat o'yin ketayotganda hisoblanadi)
execute if score #state sa.var matches 2 as @a[tag=sa.ingame,scores={sa.kill_raw=1..}] run function sniper_arena:game/on_kill
execute if score #state sa.var matches 3 run scoreboard players set @a[tag=sa.ingame] sa.kill_raw 0

# O'lgan o'yinchi qayta tug'ilgach — arenaning boshqa joyiga
execute as @e[type=minecraft:player,tag=sa.ingame,scores={sa.deaths=1..}] run function sniper_arena:game/respawn
execute as @e[type=minecraft:player,tag=sa.deathcam] run function sniper_arena:game/deathcam_tick

# Jump pad: arenadagi har qanday slime blok ustiga chiqqan o'yinchi yuqoriga otiladi
execute as @e[type=minecraft:player,tag=sa.ingame,scores={sa.jump=1..}] run function sniper_arena:game/jump_tick
execute as @e[type=minecraft:player,tag=sa.ingame,tag=!sa.deathcam] at @s if block ~ ~-0.5 ~ minecraft:slime_block unless score @s sa.jump matches 1.. run function sniper_arena:game/jump_pad

# Qurol/pichoq nazorati, arenadagi tashlangan narsalarni o'chirish
execute as @e[type=minecraft:player,tag=sa.ingame,tag=!sa.deathcam] run function sniper_arena:game/kit_check
kill @e[type=minecraft:item,x=148,y=-64,z=-89,dx=69,dy=44,dz=95]

execute if score #state sa.var matches 3 run function sniper_arena:game/end_tick
