# Har tick: qo'lda aniq 1 ta qurol va 1 ta pichoq bo'lishi kerak
execute store result score @s sa.tmp run clear @s tacz:modern_kinetic_gun 0
execute unless score @s sa.tmp matches 1 run function sniper_arena:game/kit_repair
execute if score #knife_ok sa.var matches 1 run function sniper_arena:guns/knife_check
