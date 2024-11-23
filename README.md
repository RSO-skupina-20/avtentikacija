# avtentikacija


# 1. Zagon API-ja v Dcoker okolju
- Ustvarjanje podatkovne baze `docker run --name avtentikacija-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=uporabniki -p 5434:5432 -d postgres`
- Ustvarjanje jar datoteke `mvn clean package`
- Ustvarjanje Docker slike `docker build -t avtentikacija-api .`
- Zagon Docker kontejnerja `docker run -p 8081:8080 avtentikacija-api`

# 2. Api dokumentacija
Api dokumentacija je dostopna na naslovu: `http://localhost:8081/api-specs/ui`