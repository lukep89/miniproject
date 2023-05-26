package ibf2022.batch1.project.server.JWT;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtUtil {

    // all the methods required to work with JWT

    // define a secert key (jwt signature) for JWT to be generated
    // if someone tempers with the token, the secret key will be changed and not
    // match. then an exception will be thrown to alert that token was changed.

    @Value("${jwtSecretKey}")
    private String JWT_SECRET_KEY;

    // extract the username from JWT
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // extract expiration from JWT
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    // check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // valid JWT token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        // if username is valid and token is expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // create and generate token from username (the username here is the email)
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims, username);
    }

    // TODO: IF want to add name in token. then use at frontend beside the profile
    // circle at header
    // public String generateToken(String username, String role, String name) {
    // Map<String, Object> claims = new HashMap<>();
    // claims.put("role", role);
    // claims.put("name", name);

    // return createToken(claims, username);
    // }

    private String createToken(Map<String, Object> claims, String username) {
        log.info(">>>> Inside createToken - claims & username: {} : {} ", claims, username);

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // to expire in 10hrs
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // methods to extract data from JSON in the JWT body.
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
