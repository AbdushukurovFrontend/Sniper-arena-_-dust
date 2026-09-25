# Sniper Arena — Minecraft 1.20.1 (Forge) xaritasi

Bu repo — tayyor dunyo (world) papkasi. O'yin mantig'i to'liq **datapack** orqali ishlaydi:
`datapacks/sniper_arena/`. Dunyo ochilganda datapack avtomatik yoqiladi, alohida mod yoki
plagin kerak emas (qurollar TaCZ, pichoq LR Tactical modidan olinadi).

## O'yin qanday ishlaydi

1. O'yinchi xaritaga kiradi va **lobby**ga (sakkizburchak oq xona, `115 -58 -61`) tushadi.
   Lobbida hech kim hech kimga zarar yetkaza olmaydi, qo'lda hech narsa bo'lmaydi.
2. Xona o'rtasidagi **kulrang doira**ga turiladi (atrofida aylanuvchi zarrachalar, tepasida yozuv).
   Doirada **kamida 2 o'yinchi** bo'lsa **10 soniyalik sanoq** boshlanadi. Kimdir chiqib ketsa va
   2 tadan kam qolsa — sanoq bekor bo'ladi. Sanoq paytida kirganlar ham o'yinga qo'shiladi.
3. Sanoq tugaganda doiradagilar **arenaning turli joylariga** (20 ta spawn nuqtadan, bir-biridan
   uzoq joylar tanlanadi) tushadi. Rejim — **Adventure** (Survival bilan bir xil: jon, zarar,
   yugurish, sakrash; faqat blok buzib/qo'yib bo'lmaydi). Hamma **o'zi uchun** (FFA).
4. Boshida hammada **bir xil snayper** + **CS2 karambit** (2-slot, doimiy) bo'ladi.
5. **Har bir kill** — kill qilgan o'yinchining qo'lidagi snayper keyingisiga almashadi
   (1 → 2 → … → 18-qurol, oxirgi 3 tasi sovuq qurol). O'lgan o'yinchi darhol arenaning boshqa joyida tug'iladi,
   3 soniya himoyada bo'ladi, qurol darajasi saqlanadi.
6. **Birinchi bo'lib 18 ta kill** qilgan — **g'olib**: ekranda nomi, chatda e'lon, salyut.
   6 soniyadan keyin hamma **lobbyga qaytadi** va yangi o'yin uchun yana doiraga turish mumkin.
7. **O'q cheksiz**: zaxira o'q 9999 (TaCZ `DummyAmmo`), magazin tugasa `R` bilan qayta joylanadi.

Qo'shimcha:

- **Jump pad:** arenadagi har qanday **slime blok** ustiga chiqqan o'yinchi ~7–8 blok balandga otiladi
  (koordinata kerak emas — slime blokni xohlagan joyga qo'ying).
- **Yiqilishdan zarar yo'q** (`gamerule fallDamage false`).
- **O'lim ekrani:** o'lgan o'yinchi 3 soniya kuzatuvchi rejimida bo'ladi, kamera qotilga qaraydi,
  ekranda "O'LDIRILDINGIZ — Sizni X o'ldirdi" yoziladi, keyin arenaning boshqa joyida tug'iladi.

Qo'shimcha himoyalar:

- Arenada qurol/pichoqni tashlab bo'lmaydi — darhol qaytariladi (qurol bo'sh magazin bilan,
  shunda "tashlab tez o'qlash" hiylasi ishlamaydi). Tashlangan narsalar o'chiriladi, bochkalar bo'shatiladi.
- Arenadan chiqib ketgan o'yinchi ichkariga qaytariladi; o'yinda bo'lmagan kishi arenaga kira olmaydi.
- Raqiblar o'yindan chiqib ketib 1 kishi qolsa — o'sha o'yinchi g'olib.
- Interneti uzilib qaytgan o'yinchi (o'yin hali ketayotgan bo'lsa) killari bilan o'yinga qaytadi.
- Vaqt limiti 15 daqiqa: vaqt tugasa eng ko'p kill qilgan g'olib, teng bo'lsa durang.
- O'yin paytida o'yinchilarning nomlari (nametag) ko'rinmaydi — devor ortidan snayperni bilib bo'lmaydi.
- Sidebar: lobbida umumiy **g'alabalar reytingi**, o'yinda joriy **killar**. Tepada bossbar:
  lobbida holat (doirada nechta kishi / sanoq), o'yinda qolgan vaqt.

## HUD mod (CS2 uslubidagi ekran)

`hud_mod/` — kichik Forge 1.20.1 mod (`sniper_arena_hud`). Faqat Sniper Arena ichida ishlaydi
(boshqa kartalarda oddiy ekran qoladi):

- pastdagi yurak, ovqat, tajriba chizig'i va hotbar yashiriladi;
- **o'ng pastda** qurollar ro'yxati (tanlangan qurol ajralib turadi, nomi bilan), chap pastda jon;
- **tepada o'rtada** (o'yin paytida): vaqt, g'alaba sharti va hamma o'yinchilarning yuzi + killari;
- **o'ng tepada** kill feed: `qotil [qurol] o'lgan` (o'z killaringiz qizil ramkada).

Jar faylini GitHub avtomatik yig'adi: repo → **Actions** → "Build Sniper Arena HUD mod" → oxirgi ishga tushirish →
**Artifacts** → `sniper_arena_hud`. Ichidagi `sniper_arena_hud-1.20.1-1.0.0.jar` ni barcha o'yinchilarning
`mods` papkasiga (launcher modpack'iga) qo'ying.

## Kerakli modlar (launcher modpack'ida bo'lishi shart)

| Mod | Nima uchun |
| --- | --- |
| Forge 1.20.1 (47.x) | asos |
| TaCZ (Timeless and Classics Zero) 1.1.8 | snayperlar (`tacz:modern_kinetic_gun`) |
| mcs2_gunpack | CS2 qurollari (masalan `mcs2:cs_awp_dragon_lore`) |
| LR Tactical | karambit pichog'i (`lrtactical:karambit`). Mod bo'lmasa o'yin pichoqsiz ishlayveradi |

**Muhim:** eski `mcmodhub_sniper` modini modpack'dan olib tashlang. U ham shu arena uchun
yozilgan (1v1, o'z lobbisi va doirasi bilan) — yangi datapack bilan birga ishlasa to'qnashadi.

**Sirpanish (slide):** Minecraft'da standart sirpanish yo'q. Yugurish (`Ctrl`), sakrash (`Space`),
egilish (`Shift`) va TaCZ'ning yotib olish (crawl) funksiyasi bor. Roblox'dagidek sirpanish kerak
bo'lsa, modpack'ga **ParCool!** modini qo'shish kerak (keyin uni faqat slide qoladigan qilib sozlash mumkin).

## Qurollar (18 ta, har killda keyingisi)

1. `mcs2:cs_m4a1s_emphorosaur_s`  2. `mcs2:cs_m4a1s_printstream`  3. `mcs2:cs_ak_vulcan`
4. `mcs2:cs_ak_abyssal_apparition`  5. `mcs2:cs_awp_dragon_lore`  6. `mcs2:cs_ak_asiimov`
7. `tacz:m1014`  8. `tacz:db_long`  9. `mcs2:cs_m4a4_tornado`  10. `tacz:aug`  11. `tacz:scar_l`
12. `tacz:sks_tactical`  13. `tacz:kar98` (98k optika bilan)  14. `mcs2:cs_awp_fade`  15. `mcs2:cs_glock_high_beam`
16. `lrtactical:baseball_bat`  17. `lrtactical:hardened_katana`  18. `delta_wt:shadowkiller` → 18-kill = g'alaba

Har bir qurol alohida faylda: `datapacks/sniper_arena/data/sniper_arena/functions/guns/level_1 … level_18.mcfunction`.
Karambit: `guns/knife.mcfunction`. Qurol nomini bilish: qurolni qo'lga olib `/data get entity @s SelectedItem`.
Tekshirish: `/function sniper_arena:admin/give_guns` (18 ta qurol inventarga tushadi).

**Zarar (damage), 20 jonli o'yinchiga nisbatan:**

| Qurol | Tanaga | Boshga |
| --- | --- | --- |
| M4A1-S, AK, M4A4, AUG, SCAR-L | 5 o'q | 2 o'q |
| SKS | 3 o'q | 2 o'q |
| Glock | 6 o'q | 3 o'q |
| Drobovik (m1014, db_long) | yaqindan 2 o'q, uzoqdan ko'proq | bittada o'ldirmaydi |
| AWP, kar98 (snayper) | 2 o'q | **1 o'q** |
| Sovuq qurollar | 2–3 zarba | — |

Qanday qilingan: `serverconfig/tacz-server.toml` da `DamageBaseMultiplier = 0.125` va `HeadShotBaseMultiplier = 0.65`
(mcs2 qurollari CS2 raqamlari bilan keladi: AK 36, AWP 115, boshga ×4). TaCZ qurollari uchun
`datapacks/sniper_arena/data/tacz/data/guns/` dagi fayllar shu ko'paytmaga moslab hisoblangan.
Umuman tezroq/sekinroq o'lish kerak bo'lsa — faqat `DamageBaseMultiplier` ni o'zgartiring (masalan 0.15 = tezroq).

## Sozlamalar

`datapacks/sniper_arena/data/sniper_arena/functions/config.mcfunction`:

| Qiymat | Standart | Ma'nosi |
| --- | --- | --- |
| `#kills_to_win` | 18 | g'alaba uchun kill soni |
| `#min_players` | 2 | doirada kerakli eng kam o'yinchi |
| `#countdown` | 10 | boshlanishgacha sanoq (soniya) |
| `#time_limit` | 900 | o'yin vaqti (soniya), 0 = cheksiz |
| `#end_delay` | 120 | g'olibdan keyin lobbyga qaytishgacha (tick) |

`serverconfig/tacz-server.toml` da `WeightSpeedMultiplier = -1.0` qilindi: og'ir snayper
o'yinchini sekinlashtirmaydi, hamma bir xil tezlikda yuguradi.

## Admin buyruqlari (OP / cheats yoqilgan host)

Hammasini ko'rish: `/function sniper_arena:admin/help`

| Buyruq | Vazifasi |
| --- | --- |
| `admin/builder` | qurish rejimi yoq/o'ch (creative; o'yin tizimi sizga tegmaydi). Xaritani tahrirlashdan oldin yoqing |
| `admin/status` | holat, spawnlar soni, karambit modi bor-yo'qligi |
| `admin/force_start` | doiradagilar bilan darhol boshlash (1 kishi bilan test ham bo'ladi) |
| `admin/force_stop` | o'yinni to'xtatib hammani lobbyga qaytarish |
| `admin/test_kill` | o'zingizga 1 kill yozish — qurol almashishi va 5 killda g'alabani yolg'iz tekshirish |
| `admin/give_guns` | 18 ta qurol + karambitni tekshirish uchun olish (inventar almashadi) |
| `admin/set_lobby` | lobby (kirish joyi)ni turgan joyingizga ko'chirish |
| `admin/set_pad` | o'yin boshlash doirasi markazini ko'chirish (radius 3.8 blok) |
| `admin/add_spawn` / `admin/remove_spawn` | arena spawn nuqtasi qo'shish / o'chirish |
| `admin/show_spawns` | spawn nuqtalarini zarrachalar bilan ko'rsatish/yashirish |
| `admin/reset_positions` | lobby, doira va 20 ta spawnni standart holatga qaytarish |
| `admin/reset_stats` | g'alaba/kill statistikasini tozalash |

Hamma buyruqlar `/function sniper_arena:` bilan boshlanadi, masalan `/function sniper_arena:admin/builder`.

## Tekshirish tartibi

1. Dunyoni oching — chatda xato yo'qligini, o'zingiz lobbida ekaningizni ko'ring.
2. `/function sniper_arena:admin/status` — spawnlar 20 ta, karambit "bor" bo'lishi kerak.
3. `/function sniper_arena:admin/builder` → `/function sniper_arena:admin/give_guns` — 5 qurol otadimi, o'q cheksizmi.
4. `/function sniper_arena:admin/builder` (o'chirish) → doiraga turing → `/function sniper_arena:admin/force_start` — yolg'iz test.
   Arenada `/function sniper_arena:admin/test_kill` ni 5 marta yozing: har safar qurol almashadi, 5-da g'alaba.
5. Do'st bilan: ikkalangiz doiraga turing → 10 soniya → arena → kill qilib qurol almashishini va 5 killda g'alabani tekshiring.
