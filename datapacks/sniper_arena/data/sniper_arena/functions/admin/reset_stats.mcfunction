# Umumiy statistikani nolga tushirish
scoreboard players reset * sa.wins
scoreboard players reset * sa.games
scoreboard players reset * sa.tkills
scoreboard players reset * sa.tdeaths
scoreboard players set @a sa.wins 0
scoreboard players set @a sa.games 0
scoreboard players set @a sa.tkills 0
scoreboard players set @a sa.tdeaths 0
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Statistika tozalandi.","color":"green","bold":false}]
