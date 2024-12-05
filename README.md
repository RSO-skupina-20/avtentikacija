# Avtentikacija
Mikrostoritev bo zagotavljala avtorizacijo uporabnikov in varnost le-teh. Prav tako bo ločila pravice
navadnih uporabnikov in lastnikov prostorov.

Funkcionalnosti aplikacije:
- Registracija: Registracija uporabnikov in lastnikov prostorov v sistem, omogoča ustvarjanje novih uporabniških računov
- Prijava: Varna prijava uporabnikov in lastnikov prostorov ter preverjanje avtentičnosti za dostop do sistema
- Posodobi uporabnika: Omočoga posodabljenaje podatkov o uporabniku (npr. telefon, ime, ...)
- Izbris uporabnika: Omogoča, da uporabnik izbriše svoj račun
- Pridobitev podatkov o uporabniku: Omogoča, da uporabnik vidi vse svoje podatke
- Pridobi vse uporabnike: Zgolj za namene testiranja (dostopa lahko le ADMIN)

# 1. Dodajanje .env datoteke
- Dodati je potrebno DB_URL, DB_USER, DB_PASSWORD in JWT_SECRET
- Prvi trije podatki morajo ustrezati glede na naslednji korak, ker ustvarimo bazo

# 2. Zagon API-ja v Docoker okolju (lokalno)
- Ustvarjanje podatkovne baze (za vse mikrostorive enako) `docker run --name najem-prostorov-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=najem-prostorov -p 5434:5432 -d postgres`
- Ustvarjanje jar datoteke `mvn clean package`
- Ustvarjanje Docker slike `docker build -t avtentikacija-api .`
- Zagon Docker kontejnerja na portu 8081 `docker run --env-file .env -p 8081:8080 avtentikacija-api`

# 3. Api dokumentacija
Api dokumentacija je dostopna na naslovu: `http://localhost:8081/api-specs/ui`

# 4. Api metode
- GET v1/uporabniki -  ADMINU omogoča dostop do vseh uporabnikov
- PUT v1/uporabniki - Posodobitev uporabnikovih podatkov (id se pridobi iz jwt žetona)
- POST v1/uporabniki/prijava - Prijava uporabnika
- POST v1/uporabniki/registracija - Registracija novega uporabnika
- GET v1/uporabniki/{id} - Pridobitev podatkov o nekem uporabniku
- DELETE v1/uporabniki/{id} - Izbris svojega računa
