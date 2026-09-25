# Har tick: qo'lda aniq 1 ta daraja quroli (sa_w) va 1 ta karambit (sa_k) bo'lishi kerak
execute store result score @s sa.tmp run clear @s tacz:modern_kinetic_gun{sa_w:1b} 0
execute if score #knife_ok sa.var matches 1 run function sniper_arena:guns/melee_count
execute unless score @s sa.tmp matches 1 run function sniper_arena:game/kit_repair
execute if score #knife_ok sa.var matches 1 run function sniper_arena:guns/knife_check
