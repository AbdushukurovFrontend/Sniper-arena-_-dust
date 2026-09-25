# Pichoq yo'qolgan/tashlangan bo'lsa qayta beriladi (LR Tactical bo'lsagina yuklanadi)
execute store result score @s sa.tmp run clear @s lrtactical:melee 0
execute unless score @s sa.tmp matches 1 run function sniper_arena:game/kit_repair
