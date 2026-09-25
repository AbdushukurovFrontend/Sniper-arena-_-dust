# Markerlar faqat lobby va arena chunklari (entity'lari bilan) to'liq yuklangandan keyin yaratiladi
execute if loaded 115 -58 -50 if loaded 151 -53 -84 if loaded 212 -53 1 if loaded 182 -59 -41 run function sniper_arena:setup/create_markers
execute unless score #markers sa.var matches 1 run schedule function sniper_arena:setup/check 20t
