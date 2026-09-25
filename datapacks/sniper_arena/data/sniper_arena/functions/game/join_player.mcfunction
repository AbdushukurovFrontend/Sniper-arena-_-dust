# O'yinchini o'yinga qo'shish. Arenaga joylash — arena tanlangach (mod yoki game/choose_fallback)
scoreboard players operation @s sa.match = #match_id sa.var
scoreboard players set @s sa.kills 0
scoreboard players set @s sa.kill_raw 0
scoreboard players set @s sa.deaths 0
scoreboard players set @s sa.jump 0
scoreboard players set @s sa.dead 0
scoreboard players set @s sa.killer 0
execute unless score @s sa.pid matches 1.. run function sniper_arena:player/assign_pid
scoreboard players add @s sa.games 1
team join sa.game @s
gamemode adventure @s
clear @s
effect clear @s
# ruletka paytida hech kim zarar ololmaydi
effect give @s minecraft:resistance 10 4 true
effect give @s minecraft:saturation infinite 0 true
