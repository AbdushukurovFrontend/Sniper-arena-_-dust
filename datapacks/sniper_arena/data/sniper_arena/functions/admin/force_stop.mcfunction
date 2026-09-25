# Holat 2,3,4 (o'yin, g'alaba, arena tanlash) — barchasida cheksiz aylanishni to'xtatib lobbyga qaytaradi
scoreboard players operation #tmp sa.var = #state sa.var
execute if score #tmp sa.var matches 2.. run function sniper_arena:game/abort
execute if score #tmp sa.var matches 1 run function sniper_arena:lobby/countdown_cancel
execute if score #tmp sa.var matches ..0 run tellraw @s {"text":"Hozir o'yin ketmayapti.","color":"gray"}
