execute store result score #tmp sa.var if entity @e[type=minecraft:marker,tag=sa.spawn]
execute store result score #alive sa.var if entity @a[tag=sa.ingame]
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Holat: ","color":"gray","bold":false},{"score":{"name":"#state","objective":"sa.var"},"color":"yellow","bold":false},{"text":" (0 kutish, 1 sanoq, 2 o'yin, 3 g'olib)","color":"gray","bold":false},{"text":"  |  O'yinda: ","color":"gray","bold":false},{"score":{"name":"#alive","objective":"sa.var"},"color":"yellow","bold":false},{"text":"  |  Spawnlar: ","color":"gray","bold":false},{"score":{"name":"#tmp","objective":"sa.var"},"color":"yellow","bold":false}]
execute unless entity @e[type=minecraft:marker,tag=sa.lobby] run tellraw @s {"text":"Lobby markeri YO'Q — /function sniper_arena:admin/set_lobby","color":"red"}
execute unless entity @e[type=minecraft:marker,tag=sa.pad] run tellraw @s {"text":"Doira markeri YO'Q — /function sniper_arena:admin/set_pad","color":"red"}
execute if score #knife_ok sa.var matches 1 run tellraw @s {"text":"Karambit (LR Tactical): bor","color":"green"}
execute unless score #knife_ok sa.var matches 1 run tellraw @s {"text":"Karambit (LR Tactical): mod topilmadi — pichoq berilmaydi","color":"red"}
