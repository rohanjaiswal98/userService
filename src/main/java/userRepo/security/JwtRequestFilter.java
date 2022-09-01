package userRepo.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization").split(" ")[1];

        RestTemplate restTemplate = new RestTemplate();
        String username = restTemplate.getForObject("http://localhost:8080/validateToken?token=" + requestTokenHeader, String.class);
        String role = restTemplate.getForObject("http://localhost:8080/getClaims?token=" + requestTokenHeader, String.class);

        if ((!request.getMethod().contentEquals(HttpMethod.GET.toString())) && (!role.contentEquals("admin"))) {
            response.setStatus(403);
            response.getWriter().print(username + " is not authorized for " + request.getMethod() + " /users");
            response.getWriter().flush();
            return;
        }

        if (username != null) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, null);
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        chain.doFilter(request, response);
    }

}