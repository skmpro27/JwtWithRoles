package com.example.JwtWithRoles.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.JwtWithRoles.service.JwtUserDetailService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtFilterRequest extends OncePerRequestFilter {
	
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	
	@Autowired
	private JwtUserDetailService jwtUserDetailService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authrization = request.getHeader("Authorization");
		String jwtToken = null;
		String userName = null;
		if (authrization != null && authrization.startsWith("Bearer ")) {
			jwtToken = authrization.substring(7);
			try {
				userName = jwtTokenUtils.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to geyt JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token expired");
			}
		} else {
			logger.warn("JWT TOken does not begin with Bearer");
		}
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailService.loadUserByUsername(userName);
			if (jwtTokenUtils.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken uToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				uToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(uToken);
			}
		}
		filterChain.doFilter(request, response);
	}
	

}
