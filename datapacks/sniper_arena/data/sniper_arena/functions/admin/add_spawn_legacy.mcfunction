# Arena ichida, yerda turib, o'yinchi qarashi kerak bo'lgan tomonga qarab ishlating
execute at @s run summon minecraft:marker ~ ~ ~ {Tags:["sa.marker","sa.spawn","sa.new"]}
tp @e[type=minecraft:marker,tag=sa.new] @s
tag @e[type=minecraft:marker,tag=sa.new] remove sa.new
execute at @s run forceload add ~ ~
execute at @s run particle minecraft:happy_villager ~ ~1 ~ 0.3 0.6 0.3 0 25
execute store result score #tmp sa.var if entity @e[type=minecraft:marker,tag=sa.spawn]
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Spawn nuqtasi qo'shildi. Jami: ","color":"green","bold":false},{"score":{"name":"#tmp","objective":"sa.var"},"color":"yellow","bold":false}]
