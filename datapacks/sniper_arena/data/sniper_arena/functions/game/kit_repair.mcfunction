# Qurol tashlab yuborilgan yoki yo'qolgan: qayta beriladi, lekin magazin bo'sh (tashlab "tez o'qlash" bo'lmasin)
clear @s
function sniper_arena:guns/give_current
execute if data entity @s Inventory[{Slot:0b,id:"tacz:modern_kinetic_gun"}] run item modify entity @s hotbar.0 sniper_arena:empty_mag
function sniper_arena:guns/knife
title @s actionbar {"text":"Qurolni tashlab bo'lmaydi! Qurol qaytarildi — o'qlarni qayta joylang (R).","color":"red"}
