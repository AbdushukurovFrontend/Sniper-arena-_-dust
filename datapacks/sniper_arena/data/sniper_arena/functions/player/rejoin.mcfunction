# Qaytib kirgan o'yinchi: davom etayotgan o'yinining qatnashchisi bo'lsa o'yinga, aks holda lobbyga
scoreboard players set @s sa.leave 0
scoreboard players set #ok sa.var 0
execute if entity @s[tag=sa.ingame] if score #state sa.var matches 2 if score @s sa.match = #match_id sa.var run scoreboard players set #ok sa.var 1
execute if score #ok sa.var matches 1 run function sniper_arena:game/rejoin_match
execute if score #ok sa.var matches 0 unless entity @s[tag=sa.builder] run function sniper_arena:lobby/send
