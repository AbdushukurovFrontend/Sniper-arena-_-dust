# Har tick (1/20 soniya) ishlaydi

# Yangi va qaytib kirgan o'yinchilar
execute as @a[tag=!sa.init] run function sniper_arena:player/first_join
execute as @a[scores={sa.leave=1..}] run function sniper_arena:player/rejoin

function sniper_arena:lobby/tick
execute if score #state sa.var matches 2.. run function sniper_arena:game/tick

# Soniyada bir marta
scoreboard players add #tick sa.var 1
execute if score #tick sa.var matches 20.. run function sniper_arena:second
