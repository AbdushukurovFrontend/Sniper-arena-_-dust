# Dunyoga birinchi marta kirgan o'yinchi
tag @s add sa.init
execute unless score @s sa.pid matches 1.. run function sniper_arena:player/assign_pid
scoreboard players add @s sa.wins 0
scoreboard players add @s sa.games 0
scoreboard players add @s sa.tkills 0
scoreboard players add @s sa.tdeaths 0
scoreboard players set @s sa.leave 0
scoreboard players set @s sa.deaths 0
function sniper_arena:lobby/send
tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Xush kelibsiz! O'yinni boshlash uchun o'rtadagi doiraga turing. ","color":"yellow","bold":false},{"text":"Birinchi bo'lib 5 ta kill qilgan g'olib bo'ladi.","color":"white","bold":false}]
