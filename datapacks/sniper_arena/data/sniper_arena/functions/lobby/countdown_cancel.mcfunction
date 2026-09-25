# Doirada o'yinchilar kamayib ketdi — sanoq bekor
scoreboard players set #state sa.var 0
title @a[tag=!sa.ingame,tag=!sa.builder] actionbar [{"text":"Sanoq bekor qilindi — doirada kamida ","color":"red"},{"score":{"name":"#min_players","objective":"sa.cfg"},"color":"yellow"},{"text":" o'yinchi bo'lishi kerak","color":"red"}]
execute as @a[tag=!sa.ingame,tag=!sa.builder] at @s run playsound minecraft:block.note_block.bass master @s ~ ~ ~ 1 0.6
