package si.fri.rso.skupina20.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import si.fri.rso.skupina20.entitete.Uporabnik;
import si.fri.rso.skupina20.zrna.UporabnikZrno;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import java.util.Base64;
import java.util.Date;

public class GeneriranjeJWT {
    // To je samo testni ključ, pravi ključ bo shranjen v Docker Secrets TODO spremeni
    private static String secretKey = "Z3NMa0t5YnQ1M2RzdHVmbXBVdUNuTUZkd1BkSlZ4aGIwMEtiSWFNUE4ycGNoN2lnc1BGL3lPK1l5Q2pLbEdXdUpGN2UydzZRT1l0Sk5lM3QzNlY0b0MzUWcxU2JQMEYybmhOOWZ4SndZYkZlcEt3RS93SzhkYlhxVWdQZVpCcVV2TTRBMUVrV0JFTWhyUFdkaU1ZMGJibkwvQlVnK0lZRkZBWnJIc2hwU0xiMExUZElhOEticGFacmlZNWJRck1CbUlRTThtRVh0NE9wZG54WHFsd1JzMWwyMm5xTC9XVWtDVWxhVHpjbTNxR21NZmYrSTNHSHl0aXArb2FNOTBlOGYxN1ErWnlpbTVUUllXeXJjMWt6dlp6NEhnTzZBV2oxbDNqaUtoUENSODhSTzVENVNsMXJyU2ZodWxsazB5UTBLQkZZcmczaSt1RmpuTFd3aGlNbEJ6Y3kwcVoyMm8zc1oxVjRrejVIdVdvPQ==";

    private static SecretKey getSignInKey(){
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
    public static String ustvariZeton(Uporabnik uporabnik){
        return Jwts.builder()
                .subject(uporabnik.getEmail())
                .claim("id", uporabnik.getId())
                .claim("tipUporabnika", uporabnik.getTipUporabnika())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 ur
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public static String preveriZeton(String zeton){
        try{
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(zeton)
                    .getPayload();

            if(claims.getExpiration().before(new Date())){
                return null;
            }
            String email = claims.getSubject();
            return email;
        } catch (Exception e){
            return null;
        }
    }
}
