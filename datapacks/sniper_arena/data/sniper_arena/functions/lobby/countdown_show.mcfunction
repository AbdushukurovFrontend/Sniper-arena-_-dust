function sniper_arena:lobby/countdown_text
title @a[tag=sa.onpad] times 0 25 5
title @a[tag=sa.onpad] actionbar [{"text":"O'yin boshlanishiga: ","color":"green"},{"score":{"name":"#countdown","objective":"sa.var"},"color":"yellow","bold":true},{"text":" soniya","color":"green"}]
execute if score #countdown sa.var matches ..5 run title @a[tag=sa.onpad] title {"score":{"name":"#countdown","objective":"sa.var"},"color":"gold","bold":true}
execute as @a[tag=sa.onpad] at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 1 1.4
execute if score #countdown sa.var matches ..3 as @a[tag=sa.onpad] at @s run playsound minecraft:block.note_block.pling master @s ~ ~ ~ 1 1.2
