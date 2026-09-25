# @s = kamera. O'q kelgan chiziq bo'ylab qotilga yaqinlashadi (bu chiziqda to'siq yo'q),
# juda yaqin bo'lsa (pichoq) biroz orqaga chekinadi, keyin qotilning yuziga qaraydi
scoreboard players set #steps sa.var 0
function sniper_arena:game/cam_approach
scoreboard players set #steps sa.var 0
execute at @s run function sniper_arena:game/cam_back
execute at @s run tp @s ~ ~ ~ facing entity @a[tag=sa.kcam,limit=1] eyes
