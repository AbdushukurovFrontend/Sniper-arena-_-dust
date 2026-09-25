bossbar set sniper_arena:lobby color green
execute store result bossbar sniper_arena:lobby max run scoreboard players get #countdown sa.cfg
execute store result bossbar sniper_arena:lobby value run scoreboard players get #countdown sa.var
bossbar set sniper_arena:lobby name [{"text":"O'yin boshlanishiga: ","color":"green"},{"score":{"name":"#countdown","objective":"sa.var"},"color":"yellow","bold":true},{"text":" s   ","color":"green","bold":false},{"text":"Doirada: ","color":"gray","bold":false},{"score":{"name":"#pad","objective":"sa.var"},"color":"aqua","bold":false}]
