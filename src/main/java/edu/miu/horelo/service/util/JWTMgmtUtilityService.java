package edu.miu.horelo.service.util;

import edu.miu.horelo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTMgmtUtilityService {
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Extract username (subject) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract claims (like username, expiration, etc.)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse the token and retrieve all claims
    private Claims extractAllClaims(String token) {

        /*Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())  // Ensure the correct signing key is used
                    .build()
                    .parseClaimsJws(token)  // Use parseClaimsJws for JWT parsing
                    .getBody();
        } catch (Exception e) {
            System.out.print("Could not extract claims from the token.");
            claims = null;
        }
        return claims;*/
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Method to create a new JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        var now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(24, ChronoUnit.HOURS)))  // Token expiry in 24 hours
                .signWith(getSignKey())  // Signing the token with the correct key
                .compact();
    }
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Determine the role to assign
        String role = user.getDefaultEstore() == null
                ? "USER" // Default role if no defaultEstore exists
                : user.getUserEstoreRoles().stream()
                .filter(uer -> Objects.equals(uer.getEstore().getEstoreId(), user.getDefaultEstore()))
                .map(uer -> uer.getRole().getRoleName())
                .findFirst()
                .orElse("USER"); // Fallback to USER if no role is found for the defaultEstore

        // Add the role to the claims
        claims.put("role", role);

        return createToken(claims, user.getUsername());
    }
    // Generate a token for the given username
   /* public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }*/
//
    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate if the token is correct and not expired
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Generate a signing key for JWT
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);  // Decode the base64-encoded secret key
        return Keys.hmacShaKeyFor(keyBytes);  // Return the HMAC SHA signing key
    }

    public List<String> extractRoles(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        return claims.get("roles", List.class); // Assumes roles are stored as a "roles" claim
    }

}
