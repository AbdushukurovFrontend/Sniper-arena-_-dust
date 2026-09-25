# O'lim ekrani davomida har tick: kamerani joyida ushlab, qotilga qaratib turish
scoreboard players remove @s sa.dead 1
scoreboard players operation #me sa.var = @s sa.pid
scoreboard players operation #k sa.var = @s sa.killer
execute as @e[type=minecraft:marker,tag=sa.cam] if score @s sa.pid = #me sa.var run tag @s add sa.mycam
execute if score #k sa.var matches 1.. as @a[tag=sa.ingame] if score @s sa.pid = #k sa.var run tag @s add sa.kcam
execute if entity @e[type=minecraft:marker,tag=sa.mycam] run tp @s @e[type=minecraft:marker,tag=sa.mycam,limit=1]
execute if entity @a[tag=sa.kcam] at @s run tp @s ~ ~ ~ facing entity @a[tag=sa.kcam,limit=1] eyes

execute if score @s sa.dead matches 59 if entity @a[tag=sa.kcam] run title @s actionbar [{"text":"Qotil: ","color":"gray"},{"selector":"@a[tag=sa.kcam]","color":"red"},{"text":"   Qayta tug'ilish: 3","color":"yellow"}]
execute if score @s sa.dead matches 40 if entity @a[tag=sa.kcam] run title @s actionbar [{"text":"Qotil: ","color":"gray"},{"selector":"@a[tag=sa.kcam]","color":"red"},{"text":"   Qayta tug'ilish: 2","color":"yellow"}]
execute if score @s sa.dead matches 20 if entity @a[tag=sa.kcam] run title @s actionbar [{"text":"Qotil: ","color":"gray"},{"selector":"@a[tag=sa.kcam]","color":"red"},{"text":"   Qayta tug'ilish: 1","color":"yellow"}]
execute if score @s sa.dead matches 59 unless entity @a[tag=sa.kcam] run title @s actionbar {"text":"Qayta tug'ilish: 3","color":"yellow"}
execute if score @s sa.dead matches 40 unless entity @a[tag=sa.kcam] run title @s actionbar {"text":"Qayta tug'ilish: 2","color":"yellow"}
execute if score @s sa.dead matches 20 unless entity @a[tag=sa.kcam] run title @s actionbar {"text":"Qayta tug'ilish: 1","color":"yellow"}

tag @e[type=minecraft:marker,tag=sa.mycam] remove sa.mycam
tag @a remove sa.kcam
execute if score @s sa.dead matches ..0 run function sniper_arena:game/deathcam_end
