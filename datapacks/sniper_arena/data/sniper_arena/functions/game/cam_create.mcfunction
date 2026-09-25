# Killcam kamerasi: ko'rinmas armor stand o'yinchining ko'zi balandligida yaratiladi (@s = o'lgan o'yinchi)
# Marker emas: markerlar klientga yuborilmaydi, /spectate esa klient ko'radigan entity talab qiladi
scoreboard players operation #me sa.var = @s sa.pid
execute as @e[type=minecraft:armor_stand,tag=sa.cam] if score @s sa.pid = #me sa.var run kill @s
execute anchored eyes positioned ^ ^ ^ run summon minecraft:armor_stand ~ ~ ~ {Tags:["sa.cam","sa.newcam"],Invisible:1b,Marker:1b,NoGravity:1b,Invulnerable:1b,Silent:1b,DisabledSlots:4144959}
scoreboard players operation @e[type=minecraft:armor_stand,tag=sa.newcam] sa.pid = #me sa.var
tag @e[type=minecraft:armor_stand,tag=sa.newcam] remove sa.newcam
