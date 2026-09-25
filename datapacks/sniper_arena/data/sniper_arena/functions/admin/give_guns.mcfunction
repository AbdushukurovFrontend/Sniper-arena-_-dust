# Qurollarni tekshirish: hotbar 3..7 = 1..5-qurollar, 2 = karambit
function sniper_arena:guns/level_1
item replace entity @s hotbar.2 from entity @s hotbar.0
function sniper_arena:guns/level_2
item replace entity @s hotbar.3 from entity @s hotbar.0
function sniper_arena:guns/level_3
item replace entity @s hotbar.4 from entity @s hotbar.0
function sniper_arena:guns/level_4
item replace entity @s hotbar.5 from entity @s hotbar.0
function sniper_arena:guns/level_5
item replace entity @s hotbar.6 from entity @s hotbar.0
item replace entity @s hotbar.0 with minecraft:air
function sniper_arena:guns/knife
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Hotbar 3-7: 1..5-qurollar, 2: karambit. Qurol o'rniga 'bo'sh/noma'lum' ko'rinsa — GunId noto'g'ri.","color":"yellow","bold":false}]
