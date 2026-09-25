bossbar set sniper_arena:lobby color red
bossbar set sniper_arena:lobby max 1
bossbar set sniper_arena:lobby value 1
execute store result score #alive sa.var if entity @a[tag=sa.ingame]
bossbar set sniper_arena:lobby name [{"text":"Arenada o'yin ketmoqda (","color":"red"},{"score":{"name":"#alive","objective":"sa.var"},"color":"yellow"},{"text":" o'yinchi) — tugashini kuting","color":"red"}]
