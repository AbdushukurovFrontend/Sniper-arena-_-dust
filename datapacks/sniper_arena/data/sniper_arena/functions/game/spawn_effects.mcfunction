# 100 jon, sovuq qurollar zarari x4.4 (100 jonga moslab), ochlik yo'q, to'liq jon va 3 soniyalik himoya
attribute @s minecraft:generic.max_health base set 100
# avval eskisini olib tashlaymiz: qiymat o'zgarsa ham (masalan o'yin paytida /reload) yangisi qo'llanadi
attribute @s minecraft:generic.attack_damage modifier remove 5a0c1e2d-0000-4000-8000-00000000a001
attribute @s minecraft:generic.attack_damage modifier add 5a0c1e2d-0000-4000-8000-00000000a001 sa_melee 3.4 multiply
effect give @s minecraft:saturation infinite 0 true
effect give @s minecraft:instant_health 1 5 true
effect give @s minecraft:resistance 3 4 true
