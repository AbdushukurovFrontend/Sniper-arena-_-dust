# Lobby: soniyada bir marta — doiradagilarni sanash va sanoq
tag @a remove sa.onpad
execute at @e[type=minecraft:marker,tag=sa.pad,limit=1] positioned ~ ~0.5 ~ run tag @a[tag=!sa.ingame,tag=!sa.builder,distance=..3.8] add sa.onpad
execute store result score #pad sa.var if entity @a[tag=sa.onpad]

execute if score #state sa.var matches 1 if score #pad sa.var < #min_players sa.cfg run function sniper_arena:lobby/countdown_cancel
execute if score #state sa.var matches 1 run function sniper_arena:lobby/countdown_step
execute if score #state sa.var matches 0 if score #pad sa.var >= #min_players sa.cfg run function sniper_arena:lobby/countdown_start

execute if score #state sa.var matches 0 run title @a[tag=sa.onpad] actionbar [{"text":"Doirada: ","color":"yellow"},{"score":{"name":"#pad","objective":"sa.var"},"color":"aqua","bold":true},{"text":" / ","color":"gray"},{"score":{"name":"#min_players","objective":"sa.cfg"},"color":"aqua","bold":true},{"text":"  — yana o'yinchi kutilmoqda...","color":"yellow"}]
execute if score #state sa.var matches 2.. run title @a[tag=sa.onpad] actionbar {"text":"Arenada o'yin ketmoqda — tugashini kuting...","color":"red"}

function sniper_arena:lobby/bossbar
