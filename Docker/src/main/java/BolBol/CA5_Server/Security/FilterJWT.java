package BolBol.CA5_Server.Security;

import BolBol.CA5_Server.Exceptions.IllegalException;
import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Utilities.StringUtils;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FilterJWT extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String SECRET = "bolbolestan";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("++++++++++++++++++");
        try {
            if (checkTokenJWT(request, response)) {
                Claims claims = validateToken(request);
//                System.out.println("Claims");
//                System.out.println(claims);
                if(StringUtils.hasIllegalChars(claims.getSubject())){
                    throw new IllegalException();
                }
                if (claims.get("authorities") != null) {
                    BolBolRepo.getInstance().findStudentFromEmailDB(claims.getSubject());
                    setUpSpringAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalException e) {
            System.out.println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (SQLException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private boolean checkTokenJWT(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(HEADER);
//        System.out.println("authenticationHeader");
//        System.out.println(authenticationHeader);
//        System.out.println(request);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
            return false;
        return true;
    }

    private static String getToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
        return jwtToken;
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwtToken = getToken(request);
//        System.out.println("jwtToken");
//        System.out.println(jwtToken);
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                                                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static String getTokenJWT(String email) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                    .builder()
                    .setId("softtekJWT")
                    .setSubject(email)
                    .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()) )
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + (10 * 60 * 1000) ))
                    .signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();

        return PREFIX + token;
    }
}
