# Sanoq tugadi: doiradagi hamma birinchi turga qo'shiladi
function sniper_arena:lobby/barrier_off
function sniper_arena:game/round_begin
tellraw @a [{"text":"[Sniper Arena] ","color":"gold","bold":true},{"text":"O'yin boshlandi: ","color":"yellow","bold":false},{"selector":"@a[tag=sa.ingame]","bold":false}]
