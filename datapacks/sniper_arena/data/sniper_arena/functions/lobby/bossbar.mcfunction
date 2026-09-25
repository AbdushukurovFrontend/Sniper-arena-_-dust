# Lobbydagilar uchun holat paneli
bossbar set sniper_arena:lobby players @a[tag=!sa.ingame,tag=!sa.builder]
execute if score #state sa.var matches 0 run function sniper_arena:lobby/bossbar_wait
execute if score #state sa.var matches 1 run function sniper_arena:lobby/bossbar_countdown
execute if score #state sa.var matches 2.. run function sniper_arena:lobby/bossbar_busy
