# Doiradagilar bilan darhol boshlash (test uchun 1 kishi ham bo'ladi)
execute if score #state sa.var matches 2.. run tellraw @s {"text":"O'yin allaqachon ketmoqda.","color":"red"}
execute if score #state sa.var matches ..1 run function sniper_arena:admin/force_start_run
