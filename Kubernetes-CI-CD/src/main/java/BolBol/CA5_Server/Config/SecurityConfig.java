package BolBol.CA5_Server.Config;

import BolBol.CA5_Server.Security.FilterJWT;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterAfter(new FilterJWT(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/authentication/**").permitAll()
                .anyRequest().authenticated();
    }
}
