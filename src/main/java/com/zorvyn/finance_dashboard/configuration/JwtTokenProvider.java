package com.zorvyn.finance_dashboard.configuration;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenProvider {

	 @Value("${jwt.secret}")
	    private String secretKey;

	    @Value("${jwt.expiration.ms}")
	    private long expirationMs;

	    private SecretKey getKey() {
	        return Keys.hmacShaKeyFor(secretKey.getBytes());
	    }

	    public String generateToken(Authentication auth) {
	    	
	        String authorities = populateAuthorities(auth.getAuthorities());
	        String jwt = Jwts.builder()
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(new Date().getTime() + 86400000))
	                .claim("email", auth.getName())
	                .claim("authorities", authorities)  // ← and this line
	                .signWith(getKey())
	                .compact();

	        return jwt;
	    }

	    public String getEmailFromJwtToken(String jwt) {
	        if (jwt.startsWith("Bearer ")) {
	            jwt = jwt.substring(7);
	        }
	        Claims claims = Jwts.parserBuilder()
	                .setSigningKey(getKey())
	                .build()
	                .parseClaimsJws(jwt)
	                .getBody();
	        return String.valueOf(claims.get("email"));
	    }

	    public String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
	        Set<String> auths = new HashSet<>();
	        for (GrantedAuthority authority : collection) {
	            auths.add(authority.getAuthority());
	        }
	        return String.join(",", auths);
	    }
}
