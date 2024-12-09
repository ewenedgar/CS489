package edu.miu.horelo.filter;

import edu.miu.horelo.service.impl.HoreloUserDetailsService;
import edu.miu.horelo.service.util.JWTMgmtUtilityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class  JWTAuthFilter extends OncePerRequestFilter {

    private JWTMgmtUtilityService jwtMgmtUtilityService;
    private HoreloUserDetailsService horeloUserDetailsService;

    public JWTAuthFilter(JWTMgmtUtilityService jwtMgmtUtilityService, HoreloUserDetailsService horeloUserDetailsService) {
        this.jwtMgmtUtilityService = jwtMgmtUtilityService;
        this.horeloUserDetailsService = horeloUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        // Extract the JWT token from the Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            username = jwtMgmtUtilityService.extractUsername(jwtToken);
        }

        // If a valid username is found and no authentication is set, validate the token and authenticate
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = horeloUserDetailsService.loadUserByUsername(username);

            // Validate the token and set up authentication if valid
            if (jwtMgmtUtilityService.validateToken(jwtToken, userDetails)) {

                // Optional: Extract roles if needed for further processing
                List<String> roles = jwtMgmtUtilityService.extractRoles(jwtToken);

                // Set up Spring Security authentication
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }
}
