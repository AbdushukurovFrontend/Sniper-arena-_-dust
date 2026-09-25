# Arena tanlanmoqda (holat 4): har tick
scoreboard players add #choose_t sa.var 1

# Mod arenani tanladi va o'yinchilarni joyladi (#arena_ready 1): ruletka oynasi yopilganda jang boshlanadi
execute if score #arena_ready sa.var matches 1 if score #choose_t sa.var >= #choose_ticks sa.cfg run function sniper_arena:game/go

# Mod umuman yo'q: darhol eski usulga o'tamiz (mavjud spawn markerlariga)
execute if score #state sa.var matches 4 if score #mod sa.var matches 0 if score #choose_t sa.var matches 5.. run function sniper_arena:game/choose_fallback

# Mod bor, lekin arena tanlay olmadi (masalan hali birorta arena qo'shilmagan): oyna vaqti + bufer o'tgach eski usulga
scoreboard players operation #choose_max sa.var = #choose_ticks sa.cfg
scoreboard players add #choose_max sa.var 40
execute if score #state sa.var matches 4 if score #arena_ready sa.var matches 0 if score #choose_t sa.var >= #choose_max sa.var run function sniper_arena:game/choose_fallback
