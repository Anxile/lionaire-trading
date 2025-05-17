package com.lionaire.config;

import com.lionaire.config.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

	private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwt = request.getHeader(JwtConstant.HEADER);
		
		if(jwt!=null) {
			jwt=jwt.replace(JwtConstant.TOKEN_PREFIX, ""); //remove "Bearer" prefix
			try {

				Claims claims= Jwts.parser()
						.verifyWith(key)      // replace setSigningKey
						.build()
						.parseSignedClaims(jwt)  // replace parseClaimsJws
						.getPayload();

				String email=String.valueOf(claims.get("email"));
				
				String authorities=String.valueOf(claims.get("authorities"));
				
				System.out.println("authorities -------- "+authorities);
				
				List<GrantedAuthority> auths=AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
				Authentication authentication=new UsernamePasswordAuthenticationToken(email,null, auths);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			} catch (Exception e) {
				throw new RuntimeException("invalid token...");
			}
		}
		filterChain.doFilter(request, response);
		
	}



}
