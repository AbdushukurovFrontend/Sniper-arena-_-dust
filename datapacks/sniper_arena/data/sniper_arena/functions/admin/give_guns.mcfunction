# Qurollarni tekshirish: inventar (E) ichida 1..18-qurollar tartib bilan, hotbar 2 = karambit
function sniper_arena:guns/level_1
item replace entity @s inventory.0 from entity @s hotbar.0
function sniper_arena:guns/level_2
item replace entity @s inventory.1 from entity @s hotbar.0
function sniper_arena:guns/level_3
item replace entity @s inventory.2 from entity @s hotbar.0
function sniper_arena:guns/level_4
item replace entity @s inventory.3 from entity @s hotbar.0
function sniper_arena:guns/level_5
item replace entity @s inventory.4 from entity @s hotbar.0
function sniper_arena:guns/level_6
item replace entity @s inventory.5 from entity @s hotbar.0
function sniper_arena:guns/level_7
item replace entity @s inventory.6 from entity @s hotbar.0
function sniper_arena:guns/level_8
item replace entity @s inventory.7 from entity @s hotbar.0
function sniper_arena:guns/level_9
item replace entity @s inventory.8 from entity @s hotbar.0
function sniper_arena:guns/level_10
item replace entity @s inventory.9 from entity @s hotbar.0
function sniper_arena:guns/level_11
item replace entity @s inventory.10 from entity @s hotbar.0
function sniper_arena:guns/level_12
item replace entity @s inventory.11 from entity @s hotbar.0
function sniper_arena:guns/level_13
item replace entity @s inventory.12 from entity @s hotbar.0
function sniper_arena:guns/level_14
item replace entity @s inventory.13 from entity @s hotbar.0
function sniper_arena:guns/level_15
item replace entity @s inventory.14 from entity @s hotbar.0
function sniper_arena:guns/level_16
item replace entity @s inventory.15 from entity @s hotbar.0
function sniper_arena:guns/level_17
item replace entity @s inventory.16 from entity @s hotbar.0
function sniper_arena:guns/level_18
item replace entity @s inventory.17 from entity @s hotbar.0
item replace entity @s hotbar.0 with minecraft:air
function sniper_arena:guns/knife
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Inventarni oching (E): yuqoridan boshlab 1..18-qurollar. Qurol 'noma'lum' ko'rinsa — ID noto'g'ri.","color":"yellow","bold":false}]
