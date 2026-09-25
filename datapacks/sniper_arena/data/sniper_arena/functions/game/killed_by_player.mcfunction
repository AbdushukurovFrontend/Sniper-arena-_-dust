# O'yinchi boshqa o'yinchi tomonidan o'ldirilgan zahoti (o'lgan o'yinchi nomidan) ishlaydi:
# qotilning ID raqamini eslab qoladi — o'lim ekranida uni ko'rsatish uchun
advancement revoke @s only sniper_arena:killed_by_player
scoreboard players set #k sa.var 0
execute on attacker run scoreboard players operation #k sa.var = @s sa.pid
scoreboard players operation @s sa.killer = #k sa.var
