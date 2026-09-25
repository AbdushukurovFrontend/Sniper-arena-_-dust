# Jump pad: slime blok ustiga chiqqan o'yinchini ~7-8 blok balandga otadi.
# Levitation 15-daraja roppa-rosa 12 tick (jump_tick o'chiradi) = 7.2..8.6 blok.
scoreboard players set @s sa.jump 1
effect give @s minecraft:levitation 1 14 true
playsound minecraft:entity.slime.jump master @a ~ ~ ~ 1.5 0.6
particle minecraft:item_slime ~ ~0.2 ~ 0.6 0.1 0.6 0.1 25
