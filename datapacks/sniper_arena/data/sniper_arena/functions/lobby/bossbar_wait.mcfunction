bossbar set sniper_arena:lobby color white
execute store result bossbar sniper_arena:lobby max run scoreboard players get #min_players sa.cfg
execute store result bossbar sniper_arena:lobby value run scoreboard players get #pad sa.var
bossbar set sniper_arena:lobby name [{"text":"O'yinni boshlash uchun o'rtadagi doiraga turing  ","color":"white"},{"text":"[","color":"gray"},{"score":{"name":"#pad","objective":"sa.var"},"color":"aqua"},{"text":"/","color":"gray"},{"score":{"name":"#min_players","objective":"sa.cfg"},"color":"aqua"},{"text":"]","color":"gray"}]
