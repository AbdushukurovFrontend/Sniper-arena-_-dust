
execute at @s store result score #tmp sa.var if entity @e[type=minecraft:marker,tag=sa.spawn,distance=..3]
execute if score #tmp sa.var matches 0 run tellraw @s {"text":"3 blok ichida spawn nuqtasi yo'q.","color":"red"}
execute if score #tmp sa.var matches 1.. at @s run kill @e[type=minecraft:marker,tag=sa.spawn,distance=..3,sort=nearest,limit=1]
execute store result score #tmp sa.var if entity @e[type=minecraft:marker,tag=sa.spawn]
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Qolgan spawn nuqtalari: ","color":"yellow","bold":false},{"score":{"name":"#tmp","objective":"sa.var"},"color":"yellow","bold":false}]
