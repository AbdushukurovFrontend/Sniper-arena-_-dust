# Standart joylarni yaratadi (eskilarini o'chirib).
# Lobby = sakkizburchak xona, doira = uning o'rtasidagi kulrang aylana.
kill @e[type=minecraft:marker,tag=sa.marker]
kill @e[type=minecraft:text_display,tag=sa.holo]

# Lobby (kirganda paydo bo'lish joyi), doiraga qarab turadi
summon minecraft:marker 115.5 -58 -60.5 {Tags:["sa.marker","sa.lobby"],Rotation:[0.0f,0.0f]}
# O'yinni boshlash doirasi (markazi)
summon minecraft:marker 115.5 -58 -49.5 {Tags:["sa.marker","sa.pad"]}
# Doira ustidagi yozuv (matn quyida holo_text orqali to'ldiriladi)
summon minecraft:text_display 115.5 -54.8 -49.5 {Tags:["sa.holo"],billboard:"center",alignment:"center",line_width:260,shadow:1b,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],translation:[0.0f,0.0f,0.0f],scale:[1.4f,1.4f,1.4f]},text:'""'}
function sniper_arena:lobby/holo_text

# Arena ichidagi paydo bo'lish nuqtalari. Mod bo'lsa bularni "arena1" sifatida o'ziga ko'chirib oladi
# (bir martalik migratsiya, #arenas_migrated) — shuning uchun migratsiyadan oldin har doim yaratiladi.
# Migratsiyadan keyin (arenalar endi /sa arena bilan boshqarilganda) qayta yaratilmaydi.
execute unless score #arenas_migrated sa.var matches 1 run function sniper_arena:setup/default_spawns

execute at @e[type=minecraft:marker,tag=sa.lobby,limit=1] run setworldspawn ~ ~ ~ ~
scoreboard players set #markers sa.var 1
tellraw @a[tag=sa.builder] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Lobby, doira va arena spawn nuqtalari yaratildi.","color":"green","bold":false}]
