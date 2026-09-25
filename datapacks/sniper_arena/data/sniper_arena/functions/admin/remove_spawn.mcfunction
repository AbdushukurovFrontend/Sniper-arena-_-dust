
# Mod bo'lsa arenalar /sa arena buyruqlari bilan sozlanadi
execute if score #mod sa.var matches 1 run tellraw @s [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Arenalar endi mod orqali sozlanadi: ","color":"yellow","bold":false},{"text":"/sa arena","color":"aqua","bold":false,"clickEvent":{"action":"suggest_command","value":"/sa arena "}},{"text":" (ro'yxat: /sa arena list)","color":"gray","bold":false}]
execute unless score #mod sa.var matches 1 run function sniper_arena:admin/remove_spawn_legacy
