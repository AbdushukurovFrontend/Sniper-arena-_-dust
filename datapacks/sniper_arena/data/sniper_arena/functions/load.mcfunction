# Sniper Arena — dunyo ochilganda va /reload da ishlaydi

# --- Scoreboard ---
scoreboard objectives add sa.var dummy
scoreboard objectives add sa.cfg dummy
scoreboard objectives add sa.tmp dummy
scoreboard objectives add sa.match dummy
scoreboard objectives add sa.kill_raw playerKillCount
scoreboard objectives add sa.deaths deathCount
scoreboard objectives add sa.leave minecraft.custom:minecraft.leave_game
scoreboard objectives add sa.kills dummy {"text":"⚔ KILLAR ⚔","color":"red","bold":true}
scoreboard objectives add sa.wins dummy {"text":"★ G'ALABALAR ★","color":"gold","bold":true}
scoreboard objectives add sa.games dummy
scoreboard objectives add sa.tkills dummy
scoreboard objectives add sa.tdeaths dummy
scoreboard objectives add sa.jump dummy
scoreboard objectives add sa.dead dummy
scoreboard objectives add sa.killer dummy
scoreboard objectives add sa.pid dummy

function sniper_arena:config

# --- Jamoalar: lobbida bir-biriga zarar yo'q, o'yinda hamma hamma bilan (FFA) ---
team add sa.lobby
team modify sa.lobby color aqua
team modify sa.lobby friendlyFire false
team modify sa.lobby collisionRule never
team add sa.game
team modify sa.game color red
team modify sa.game friendlyFire true
team modify sa.game nametagVisibility never
team modify sa.game deathMessageVisibility hideForOtherTeams

# Sidebar: lobbida g'alabalar reytingi, o'yinda joriy killar
scoreboard objectives setdisplay sidebar.team.aqua sa.wins
scoreboard objectives setdisplay sidebar.team.red sa.kills

# --- Bossbarlar ---
bossbar add sniper_arena:lobby ""
bossbar add sniper_arena:game ""
bossbar set sniper_arena:game color red

# --- Dunyo qoidalari ---
gamerule keepInventory true
gamerule fallDamage false
# Jon o'z-o'zidan tez to'lmaydi: o'yinda har #regen_every soniyada +4 jon (game/regen)
gamerule naturalRegeneration false
gamerule doImmediateRespawn true
gamerule spawnRadius 0
gamerule doMobSpawning false
gamerule doPatrolSpawning false
gamerule doTraderSpawning false
gamerule doWardenSpawning false
gamerule doInsomnia false
gamerule disableRaids true
gamerule mobGriefing false
gamerule doFireTick false
gamerule doDaylightCycle false
gamerule doWeatherCycle false
gamerule announceAdvancements false
gamerule showDeathMessages true
gamerule commandBlockOutput false
time set noon
weather clear

# Lobby va arena chunklari doim yuklangan tursin (markerlar ishlashi uchun)
forceload add 96 -70 136 -30
forceload add 146 -90 218 8

# --- Holat ---
scoreboard players set #tick sa.var 0
execute unless entity @a[tag=sa.ingame] run scoreboard players set #state sa.var 0
execute unless entity @a[tag=sa.ingame] run kill @e[type=minecraft:armor_stand,tag=sa.cam]
# Eski versiyadagi kamera markerlari
kill @e[type=minecraft:marker,tag=sa.cam]

# Karambit (LR Tactical) o'rnatilganmi? Mod bo'lmasa knife_probe yuklanmaydi va 0 qoladi
scoreboard players set #knife_ok sa.var 0
function sniper_arena:guns/knife_probe

# Doira ustidagi yozuvni yangilash
execute as @e[type=minecraft:text_display,tag=sa.holo] run data merge entity @s {text:'[{"text":"SNIPER ARENA\\n","color":"gold","bold":true},{"text":"O\'yinni boshlash uchun\\ndoira ichiga turing\\n","color":"white","bold":false},{"text":"(kamida 2 o\'yinchi, 18 kill = g\'alaba)","color":"gray","bold":false}]'}

# Lobby / doira / spawn markerlari (birinchi marta avtomatik yaratiladi)
execute at @e[type=minecraft:marker,tag=sa.lobby,limit=1] run setworldspawn ~ ~ ~ ~
execute unless score #markers sa.var matches 1 run schedule function sniper_arena:setup/check 20t

tellraw @a[tag=sa.builder] [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"Datapack yuklandi. Buyruqlar: ","color":"yellow","bold":false},{"text":"/function sniper_arena:admin/help","color":"aqua","bold":false,"clickEvent":{"action":"suggest_command","value":"/function sniper_arena:admin/help"}}]
