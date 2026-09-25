# Qurish rejimini yoqish/o'chirish
execute store success score #tmp sa.var if entity @s[tag=sa.builder]
execute if score #tmp sa.var matches 1 run function sniper_arena:admin/builder_off
execute if score #tmp sa.var matches 0 run function sniper_arena:admin/builder_on
