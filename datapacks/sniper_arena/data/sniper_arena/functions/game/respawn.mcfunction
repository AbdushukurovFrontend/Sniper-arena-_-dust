# O'lgan o'yinchi qayta tug'ildi: avval 3 soniyalik o'lim ekrani (qotil ko'rsatiladi), keyin arenaga
scoreboard players set @s sa.deaths 0
scoreboard players set @s sa.jump 0
scoreboard players add @s sa.tdeaths 1
function sniper_arena:game/deathcam_start
