# Internet uzilib qaytgan o'yinchi davom etayotgan o'yiniga qaytadi (killari saqlanadi)
scoreboard players set @s sa.kill_raw 0
scoreboard players set @s sa.deaths 0
scoreboard players set @s sa.jump 0
scoreboard players set @s sa.dead 0
scoreboard players set @s sa.killer 0
tag @s remove sa.deathcam
scoreboard players operation #me sa.var = @s sa.pid
execute as @e[type=minecraft:marker,tag=sa.cam] if score @s sa.pid = #me sa.var run kill @s
team join sa.game @s
gamemode adventure @s
effect clear @s
function sniper_arena:game/spawn_random
execute at @s run spawnpoint @s ~ ~ ~ ~
function sniper_arena:game/kit
function sniper_arena:game/spawn_effects
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"O'yinga qaytdingiz!","color":"green","bold":false}]
