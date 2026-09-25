# Spawn nuqtalarini ko'rsatish/yashirish
execute store success score #tmp sa.var if score #show_spawns sa.var matches 1
execute if score #tmp sa.var matches 1 run scoreboard players set #show_spawns sa.var 0
execute if score #tmp sa.var matches 0 run scoreboard players set #show_spawns sa.var 1
execute if score #show_spawns sa.var matches 1 run tellraw @s {"text":"Spawnlar ko'rsatilmoqda: oq ustun = spawn, olov = qaragan tomoni, yashil = lobby, sariq = doira.","color":"green"}
execute if score #show_spawns sa.var matches 0 run tellraw @s {"text":"Spawnlar yashirildi.","color":"gray"}
