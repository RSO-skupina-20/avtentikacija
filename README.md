# Avtentikacija

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