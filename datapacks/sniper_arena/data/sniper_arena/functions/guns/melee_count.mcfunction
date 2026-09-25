# Sovuq qurol darajasidagi (13-15) qurolni ham sanaydi (LR Tactical bo'lsagina yuklanadi)
execute store result score #m sa.var run clear @s lrtactical:melee{sa_w:1b} 0
scoreboard players operation @s sa.tmp += #m sa.var
