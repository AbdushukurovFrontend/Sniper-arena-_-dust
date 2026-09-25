# Otilgan o'yinchi: 12 tickdan keyin ko'tarilish to'xtaydi, 1.5 soniyadan keyin pad yana ishlaydi
scoreboard players add @s sa.jump 1
execute if score @s sa.jump matches 13 run effect clear @s minecraft:levitation
execute if score @s sa.jump matches 30.. run scoreboard players set @s sa.jump 0
