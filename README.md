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
   (1 → 2 → 3 → 4 → 5-qurol). O'lgan o'yinchi darhol arenaning boshqa joyida tug'iladi,
   3 soniya himoyada bo'ladi, qurol darajasi saqlanadi.
6. **Birinchi bo'lib 5 ta kill** qilgan — **g'olib**: ekranda nomi, chatda e'lon, salyut.
   6 soniyadan keyin hamma **lobbyga qaytadi** va yangi o'yin uchun yana doiraga turish mumkin.
7. **O'q cheksiz**: zaxira o'q 9999 (TaCZ `DummyAmmo`), magazin tugasa `R` bilan qayta joylanadi.

Qo'shimcha himoyalar:

- Arenada qurol/pichoqni tashlab bo'lmaydi — darhol qaytariladi (qurol bo'sh magazin bilan,
  shunda "tashlab tez o'qlash" hiylasi ishlamaydi). Tashlangan narsalar o'chiriladi, bochkalar bo'shatiladi.
- Arenadan chiqib ketgan o'yinchi ichkariga qaytariladi; o'yinda bo'lmagan kishi arenaga kira olmaydi.
- Raqiblar o'yindan chiqib ketib 1 kishi qolsa — o'sha o'yinchi g'olib.
- Interneti uzilib qaytgan o'yinchi (o'yin hali ketayotgan bo'lsa) killari bilan o'yinga qaytadi.
- Vaqt limiti 10 daqiqa: vaqt tugasa eng ko'p kill qilgan g'olib, teng bo'lsa durang.
- O'yin paytida o'yinchilarning nomlari (nametag) ko'rinmaydi — devor ortidan snayperni bilib bo'lmaydi.
- Sidebar: lobbida umumiy **g'alabalar reytingi**, o'yinda joriy **killar**. Tepada bossbar:
  lobbida holat (doirada nechta kishi / sanoq), o'yinda qolgan vaqt.

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

## Qurollarni almashtirish

Qurollar shu fayllarda (har birida bitta qator):

```
datapacks/sniper_arena/data/sniper_arena/functions/guns/level_1.mcfunction   ← boshida hammada (0 kill)
datapacks/sniper_arena/data/sniper_arena/functions/guns/level_2.mcfunction   ← 1 killdan keyin
datapacks/sniper_arena/data/sniper_arena/functions/guns/level_3.mcfunction   ← 2 killdan keyin
datapacks/sniper_arena/data/sniper_arena/functions/guns/level_4.mcfunction   ← 3 killdan keyin
datapacks/sniper_arena/data/sniper_arena/functions/guns/level_5.mcfunction   ← 4 killdan keyin (keyingi kill = g'alaba)
datapacks/sniper_arena/data/sniper_arena/functions/guns/knife.mcfunction     ← doimiy pichoq
```

Hozirgi (vaqtincha) qurollar: `tacz:m700`, `tacz:kar98`, `tacz:ai_awp`, `tacz:m95`,
`mcs2:cs_awp_dragon_lore`. Faqat `GunId:"..."` ichidagi nomni almashtirish kifoya.
Qurol nomini bilish: qurolni qo'lga olib `/data get entity @s SelectedItem.tag.GunId` yozing.
O'zgartirgach o'yinda `/reload` yozing va `/function sniper_arena:admin/give_guns` bilan tekshiring.

## Sozlamalar

`datapacks/sniper_arena/data/sniper_arena/functions/config.mcfunction`:

| Qiymat | Standart | Ma'nosi |
| --- | --- | --- |
| `#kills_to_win` | 5 | g'alaba uchun kill soni |
| `#min_players` | 2 | doirada kerakli eng kam o'yinchi |
| `#countdown` | 10 | boshlanishgacha sanoq (soniya) |
| `#time_limit` | 600 | o'yin vaqti (soniya), 0 = cheksiz |
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
| `admin/give_guns` | 5 ta qurol + karambitni tekshirish uchun olish (hotbar 1–7 almashadi) |
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
5. Do'st bilan: ikkalangiz doiraga turing → 10 soniya → arena → kill qilib qurol almashishini va 5 killda g'alabani tekshiring.
