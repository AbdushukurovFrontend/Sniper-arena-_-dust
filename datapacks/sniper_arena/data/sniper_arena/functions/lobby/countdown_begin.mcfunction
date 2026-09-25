scoreboard players set #state sa.var 1
scoreboard players operation #countdown sa.var = #countdown sa.cfg
tellraw @a[tag=!sa.ingame,tag=!sa.builder] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"O'yin boshlanmoqda! Qatnashish uchun o'rtadagi doiraga kiring.","color":"green","bold":false}]
function sniper_arena:lobby/countdown_show
