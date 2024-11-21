package si.fri.rso.skupina20.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import si.fri.rso.skupina20.entitete.Uporabnik;

public class GenerirajZeton {

    private static String secretKey = "mySecret";

    // Ustvairi žeton
    public static String createToken(Uporabnik uporabnik){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withClaim("id", uporabnik.getId())
                .withClaim("email", uporabnik.getEmail())
                .withClaim("tipUporabnika", uporabnik.getTipUporabnika().toString())
                .withClaim("role", "uporabnik")
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 3600000))
                .withIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .sign(algorithm);
        return token;
    }

    // Preveri žeton
    public static boolean verifyToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
