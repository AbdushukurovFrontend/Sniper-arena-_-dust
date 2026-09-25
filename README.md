# Sniper Arena — Minecraft 1.20.1 (Forge) xaritasi

Bu repo — tayyor dunyo (world) papkasi. O'yin mantig'i to'liq **datapack** orqali ishlaydi:
`datapacks/sniper_arena/`. Dunyo ochilganda datapack avtomatik yoqiladi, alohida mod yoki
plagin kerak emas (qurollar TaCZ, pichoq LR Tactical modidan olinadi).

## O'yin qanday ishlaydi

1. O'yinchi xaritaga kiradi va **lobby**ga (sakkizburchak oq xona, `115 -58 -61`) tushadi.
   Lobbida hech kim hech kimga zarar yetkaza olmaydi, qo'lda hech narsa bo'lmaydi.
2. Xona o'rtasidagi **kulrang doira**ga turiladi (atrofida aylanuvchi zarrachalar, tepasida yozuv).
   Doirada **kamida 2 o'yinchi** bo'lsa, doira atrofida **qizil shaffof devor** paydo bo'ladi (bu shunchaki
   ko'rinish — undan bemalol o'tib bo'ladi, to'siq emas) va ustida katta harflar bilan **10 soniyalik sanoq**
   ko'rsatiladi. Kimdir chiqib ketsa va 2 tadan kam qolsa — sanoq bekor bo'ladi. Sanoq paytida doiraga kirgan
   har kim ham o'yinga qo'shiladi — nechta odam bo'lishidan qat'iy nazar, **hamma birga** o'ynaydi.
3. Sanoq tugagach hammaga **arena tanlash oynasi** ochiladi: sozlangan arenalar ro'yxati chapdan o'ngga
   aylanadi (case-opening uslubida) va bir necha soniyadan keyin bittasida to'xtaydi — o'sha arenaga hamma
   birga tushadi. Rejim — **Adventure** (Survival bilan bir xil: jon, zarar, yugurish, sakrash; faqat blok
   buzib/qo'yib bo'lmaydi). Hamma **o'zi uchun** (FFA, jamoa yo'q).
4. Boshida hammada **bir xil snayper** + **CS2 karambit** (2-slot, doimiy) bo'ladi.
5. **Har bir kill** — kill qilgan o'yinchining qo'lidagi snayper keyingisiga almashadi
   (1 → 2 → … → 15-qurol, oxirgi 3 tasi sovuq qurol). O'lgan o'yinchi darhol arenaning boshqa joyida tug'iladi,
   3 soniya himoyada bo'ladi, qurol darajasi saqlanadi.
6. **Birinchi bo'lib 15 ta kill** qilgan — **g'olib**: ekranda nomi, chatda e'lon, salyut. Hamma **boshlash
   joyiga qaytmaydi** — bir necha soniyadan keyin yana arena tanlash oynasi ochiladi va **yangi tur** (yangi
   arena, killar 0 dan) boshlanadi. Shu tarzda cheksiz davom etadi — bitta arenada zerikib qolmaslik uchun.
   Shu payt doirada turgan yangi o'yinchilar ham qo'shiladi. To'xtatish: `admin/force_stop` (hammani lobbyga
   qaytaradi).
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

- boshqa o'yinchilar qurolidagi **skin ko'rinadi**: mod TaCZ'ning `GunLodRenderDistance` sozlamasini 128 ga qo'yadi
  (standart 0 da boshqalar qurolini oddiy, skinsiz model bilan ko'rardingiz).
- **virtual o'lim** (1.1.0 dan): arenada o'lgan o'yinchi haqiqatda o'lmaydi va respawn bo'lmaydi. Minecraft 1.20.1 da
  har respawnda server o'yinchiga atrofdagi hamma chunklarni qaytadan yuboradi — lider (host) buni sezmaydi, lekin
  internet orqali kirgan o'yinchiga har o'limda katta paket ketadi: ping ko'tariladi, killcam kechikadi va u
  "Timed out" bilan chiqib ketadi. Endi o'lim faqat hisobga yoziladi (kill, advancement, statistika aynan vanilladagidek),
  o'yinchi darhol kuzatuvchi rejimiga o'tadi va killcam boshlanadi. O'chirish: `#virtual_death` (config.mcfunction).

- **qon effekti**: o'q tekkanda ekranning turli joylariga qon sachraydi (zarar qancha katta — shuncha ko'p),
  jon kamaygan sari ekran chetlaridan qon bosib keladi, 30% dan kam jonda ekranda qon qoladi va yurak urishidek
  pulslanadi. Jon to'lgan sari qon kamayib, yo'qolib boradi. Teksturalar dasturiy yasalgan: `hud_mod/tools/gen_blood.py`.

- **arena tanlash oynasi** (2.0.0 dan): har tur boshida barcha sozlangan arenalar ekran bo'ylab aylanib,
  bittasida to'xtaydi. Server allaqachon tanlab bo'lgan — bu ekran shunchaki ko'rsatish uchun; ESC bilan
  yopilmaydi, o'zi 1-2 soniyadan keyin yopiladi.

Killcam: kamera o'lgan joyda yaratiladi, o'q kelgan chiziq bo'ylab qotilgacha ~4 blok qolguncha yaqinlashadi
(bu chiziqda devor yo'q, shuning uchun qotil doim ko'rinadi) va `/spectate` bilan qulflanadi — sichqoncha, shift
yoki sichqoncha tugmasi bilan ko'rinish buzilmaydi.

Jar faylini GitHub avtomatik yig'adi va **Release**ga qo'yadi: repo → **Releases** → oxirgi `hud-mod-v...` →
`sniper_arena_hud-1.20.1-2.0.0.jar`ni yuklab oling. Buni **barcha o'yinchilarning, albatta liderning (host) ham**
`mods` papkasiga (launcher modpack'iga) qo'ying — virtual o'lim va arenalar hostdagi serverda ishlaydi.
Hamma bir xil versiyani qo'yishi kerak (eskisi bilan yangi versiyadagi o'yinchi bir serverga kira olmaydi).

## Arenalar (ko'p arena, har turda tasodifiy tanlanadi)

O'yin endi bitta arenaga bog'liq emas — istalgancha arena qo'shish mumkin, har tur boshida ulardan biri
tasodifiy tanlanadi (ketma-ket bitta arena ikki marta tushmaydi, agar boshqa arena mavjud bo'lsa). Buni
**mod** boshqaradi (datapack emas): arenalar ro'yxati dunyo bilan birga saqlanadi, `/reload`da yo'qolmaydi.

**Yangi arena qo'shish** (WorldEdit/schematic bilan qurib bo'lgach):

1. `/function sniper_arena:admin/builder` — qurish rejimini yoqing (o'yin tizimi sizga tegmaydi).
2. `/sa arena create <id> <nomi>` — masalan `/sa arena create desert Cho'l arenasi`. `<id>` — bitta so'z
   (ichki nom), `<nomi>` — arena tanlash oynasida ko'rinadigan nom (bo'shliq bilan bo'lishi mumkin).
3. Arena ichida turli joylarga borib, har birida `/sa arena addspawn desert` yozing (qarab turgan tomoningiz
   saqlanadi — o'yinchi shu tomonga qarab tug'iladi). **Kamida 6–8 ta**, tavsiya 12–20 ta spawn nuqta.
4. (Ixtiyoriy) `/sa arena icon desert minecraft:sand` — ruletka oynasida shu arena uchun rasm.
5. `/sa arena list` — ro'yxatni va spawnlar sonini tekshirish. `/sa roulette` — ruletka oynasini o'zingizda
   sinab ko'rish (o'yinga ta'sir qilmaydi).

**Muhim: arenalarni bir-biridan va lobbydan kamida ~300 blok uzoqqa qo'ying.** Server faqat hozir
o'ynalayotgan (yoki arena tanlanayotgan paytda — barcha nomzod arenalarni) chunklarni yuklaydi, qolganlari
umuman yuklanmaydi — shuning uchun arenalar soni ko'p bo'lsa ham lag qo'shmaydi. Lekin yaqin qo'yilsa (masalan
50 blok orasida), ular bir vaqtda chizilib ketishi mumkin va zaif kompyuterda FPS pasayadi.

Boshqa buyruqlar: `/sa arena removespawn <id>` (3 blok ichidagi eng yaqinini o'chiradi), `/sa arena show <id>`
(spawnlarni zarracha bilan ko'rsatadi), `/sa arena tp <id>` (o'sha arenaga boring), `/sa arena info <id>`,
`/sa arena enable|disable <id>` (o'chirilgan arena ruletkada chiqmaydi, lekin saqlanib qoladi),
`/sa arena remove <id>` (butunlay o'chiradi), `/sa arena pos1|pos2 <id>` (ixtiyoriy: arena chegarasini qo'lda
belgilash — bo'lmasa spawnlar atrofidan avtomatik hisoblanadi).

Xaritadagi **asl (birinchi) arena** dunyo birinchi marta ochilganda avtomatik "arena1" nomi bilan ro'yxatga
o'tkaziladi (eski 20 ta spawn nuqtasi bilan) — hech narsa yo'qolmaydi, shunchaki endi u ham ro'yxatdagi
bitta arena hisoblanadi.

Agar mod o'rnatilmagan bo'lsa yoki hali birorta arena qo'shilmagan bo'lsa, o'yin eski usulda (bitta arena,
`admin/add_spawn` bilan qo'lda qo'yilgan spawnlar) ishlayveradi.

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

## Qurollar (15 ta, har killda keyingisi)

1. `mcs2:cs_m4a1s_emphorosaur_s`  2. `mcs2:cs_ak_vulcan`  3. `mcs2:cs_ak_abyssal_apparition`
4. `mcs2:cs_awp_dragon_lore`  5. `tacz:m1014`  6. `tacz:db_long`  7. `mcs2:cs_m4a4_tornado`
8. `tacz:aug`  9. `tacz:scar_l`  10. `tacz:sks_tactical`  11. `tacz:kar98` (98k optika bilan)
12. `mcs2:cs_glock_high_beam`  13. `lrtactical:baseball_bat`  14. `lrtactical:hardened_katana`
15. `delta_wt:shadowkiller` → 15-kill = g'alaba

(Avval 18 ta edi; ikkinchi M4A1-S skin, uchinchi AK skin va ikkinchi AWP skinni olib tashladik — bir xil
qurolning ikkinchi skinini qoldirmasdan, har killda haqiqatda BOSHQA qurol kelishi uchun. Boshqa 3 tasini
xohlasangiz — screenshot bilan ayting, o'zgartirib beraman.)

Har bir qurol alohida faylda: `datapacks/sniper_arena/data/sniper_arena/functions/guns/level_1 … level_15.mcfunction`.
Karambit: `guns/knife.mcfunction`. Qurol nomini bilish: qurolni qo'lga olib `/data get entity @s SelectedItem`.
Tekshirish: `/function sniper_arena:admin/give_guns` (15 ta qurol inventarga tushadi).

**Jon va zarar (o'yinchida 100 jon):**

| Qurol | Tanaga | Boshga |
| --- | --- | --- |
| M4A1-S, AK, M4A4, AUG, SCAR-L, Glock | 3–4 o'q | 2 o'q |
| SKS | 3 o'q | 2 o'q |
| Drobovik (m1014, db_long) | yaqindan 2 o'q, uzoqdan ko'proq | bittada o'ldirmaydi |
| **AWP** | **1 o'q** (oyoqdan boshqa har qanday joyga) | **1 o'q** |
| kar98 | 2 o'q | **1 o'q** |
| Pichoq va sovuq qurollar (zarar x4.4) | 3–4 zarba | — |

- **AWP (CS2 kabi):** o'q oyoqdan yuqoriga — tana, qo'l, bosh — tegsa bitta o'qda o'ldiradi; oyoqqa tegsa zarar
  x0.75 va to'liq jondan bitta o'q bilan o'lmaydi. Buni HUD mod serverda (hostda) hisoblaydi: o'q chizig'i nishonning
  qaysi balandligidan o'tganiga qaraydi (pastki 37.5% — oyoq).
- Jon o'z-o'zidan tez to'lmaydi: o'yinda **har 5 soniyada +4 jon** (`config.mcfunction` → `#regen_every`).
- Umumiy o'q zarari: `serverconfig/tacz-server.toml` → `DamageBaseMultiplier = 0.903`, `HeadShotBaseMultiplier = 0.65`
  (mcs2 qurollari CS2 raqamlari bilan keladi). Tezroq o'lish kerak bo'lsa `DamageBaseMultiplier` ni oshiring.
- Sovuq qurollar zarari: `game/spawn_effects.mcfunction` → `sa_melee 3.4 multiply` (x4.4).
- TaCZ qurollari uchun `datapacks/sniper_arena/data/tacz/data/guns/` dagi fayllar shu ko'paytmaga moslab hisoblangan.

## Sozlamalar

`datapacks/sniper_arena/data/sniper_arena/functions/config.mcfunction`:

| Qiymat | Standart | Ma'nosi |
| --- | --- | --- |
| `#kills_to_win` | 15 | g'alaba uchun kill soni |
| `#min_players` | 2 | doirada kerakli eng kam o'yinchi |
| `#countdown` | 10 | boshlanishgacha sanoq (soniya) |
| `#choose_ticks` | 100 | arena tanlash (ruletka) oynasi davomiyligi (tick, 100 = 5 soniya) |
| `#time_limit` | 900 | tur vaqti (soniya), 0 = cheksiz — vaqt tugasa eng ko'p kill qilgan g'olib (keyingi tur boshlanadi) |
| `#end_delay` | 120 | g'olib e'lon qilingandan keyin keyingi tur boshlanishigacha (tick) |
| `#virtual_death` | 1 | 0 qilsangiz oddiy (respawnli) o'lim — internet orqali kirganlarga tavsiya etilmaydi |

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
| `admin/test_kill` | o'zingizga 1 kill yozish — qurol almashishi va g'alabani yolg'iz tekshirish |
| `admin/give_guns` | 15 ta qurol + karambitni tekshirish uchun olish (inventar almashadi) |
| `admin/set_lobby` | lobby (kirish joyi)ni turgan joyingizga ko'chirish |
| `admin/set_pad` | o'yin boshlash doirasi markazini ko'chirish (radius 3.8 blok) |
| `admin/reset_stats` | g'alaba/kill statistikasini tozalash |

Mod o'rnatilmagan bo'lsa (eski, bitta arenali usul) yana: `admin/add_spawn` / `admin/remove_spawn` (arena
spawn nuqtasi qo'shish/o'chirish), `admin/show_spawns` (ko'rsatish/yashirish), `admin/reset_positions`
(lobby, doira va 20 ta spawnni standart holatga qaytarish). Mod bor bo'lsa arenalar `/sa arena` bilan
boshqariladi (yuqoridagi "Arenalar" bo'limi).

Hamma `admin/...` buyruqlar `/function sniper_arena:` bilan boshlanadi, masalan
`/function sniper_arena:admin/builder`. `/sa` buyruqlari esa to'g'ridan-to'g'ri yoziladi (mod bergan).

## Tekshirish tartibi

1. Dunyoni oching — chatda xato yo'qligini, o'zingiz lobbida ekaningizni ko'ring.
2. `/function sniper_arena:admin/status` — "Mod: bor" (agar HUD modni qo'ygan bo'lsangiz), karambit "bor" bo'lishi kerak.
3. `/sa arena list` — kamida bitta arena ko'rinishi kerak (birinchi ochilishda "arena1" avtomatik qo'shiladi).
4. `/function sniper_arena:admin/builder` → `/function sniper_arena:admin/give_guns` — o'qlar otadimi, o'q cheksizmi.
5. `/function sniper_arena:admin/builder` (o'chirish) → doiraga turing → `/function sniper_arena:admin/force_start` —
   yolg'iz test. Sanoq → qizil devor → arena tanlash oynasi → arenaga tushasiz. Arenada
   `/function sniper_arena:admin/test_kill` ni bir necha marta yozing: har safar qurol almashadi, 15-da g'alaba —
   va boshlash joyiga qaytmasdan, yana arena tanlash oynasi ochilib, yangi tur boshlanishini tekshiring.
   To'xtatish: `/function sniper_arena:admin/force_stop`.
6. Do'st bilan: ikkalangiz doiraga turing → 10 soniya → arena tanlash → arena → kill qilib qurol almashishini,
   15 killda g'alabani va keyingi turga avtomatik o'tishini tekshiring.
7. Yangi arena qo'shgan bo'lsangiz: bir necha marta test o'yin o'tkazib, ruletkada u ham chiqishini ko'ring
   (`/sa roulette` bilan tezroq, o'yinsiz ham sinab ko'rish mumkin).
