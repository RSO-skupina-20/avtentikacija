package si.fri.rso.skupina20.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import si.fri.rso.skupina20.entitete.Uporabnik;

import java.util.List;

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
    public static boolean verifyToken(String token, List<String> roles){
        try {
            // Odstrani Bearer iz tokena
            token = token.replace("Bearer ", "");
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWT.require(algorithm).build().verify(token);
            DecodedJWT jwt = JWT.decode(token);

            String tipUporabnika = jwt.getClaim("tipUporabnika").asString();

            if (roles.contains(tipUporabnika)){
                return true;
            }
        } catch (Exception e){
            return false;
        }
        return false;
    }
}
