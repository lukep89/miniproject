package ibf2022.batch1.project.server.JWT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// class to check if user has a valid jwt token

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsSvc;

    Claims claims = null;
    private String userName = null;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // if httprequest matches these paths
        if (request.getServletPath()
                .matches("/api/user/login|/api/user/signup|/api/user/forgotPassword")) {
            // .matches("/api/user/login|/api/user/forgotPassword")) { // for testing

            // no token validation required
            filterChain.doFilter(request, response);

        } else {
            String authHeader = request.getHeader("Authorization");
            log.info(">>>> Inside doFilterInternal - authHeader: {}", authHeader);

            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                userName = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);

                log.info(">>>> Inside doFilterInternal - token, userName, claims : {} , {} , {}", token, userName,
                        claims);
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customerUserDetailsSvc.loadUserByUsername(userName);

                log.info(">>>> Inside doFilterInternal - userDetails : {}", userDetails);

                if (jwtUtil.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info(">>>> Inside doFilterInternal - authToken : {}", authToken);
                }
            }
            filterChain.doFilter(request, response);
        }

    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) claims.get("role"));

    }

    public boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUser() {
        return userName;
    }

}
