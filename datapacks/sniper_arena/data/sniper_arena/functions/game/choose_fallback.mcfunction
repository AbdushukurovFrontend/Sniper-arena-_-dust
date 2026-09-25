# Arena mod orqali tanlanmadi: mavjud spawn markerlari bo'lsa shularga joylab darhol boshlaymiz
execute unless entity @e[type=minecraft:marker,tag=sa.spawn] run tellraw @a[tag=sa.ingame] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Arena topilmadi! Admin: /sa arena create <nom> va /sa arena addspawn <nom>","color":"red","bold":false}]
execute unless entity @e[type=minecraft:marker,tag=sa.spawn] run function sniper_arena:game/abort
execute if score #state sa.var matches 4 run scoreboard players set #arena_ready sa.var 1
execute if score #state sa.var matches 4 as @a[tag=sa.ingame] run function sniper_arena:game/spawn_random
execute if score #state sa.var matches 4 run function sniper_arena:game/go
