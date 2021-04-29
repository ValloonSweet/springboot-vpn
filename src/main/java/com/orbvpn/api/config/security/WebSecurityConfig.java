package com.orbvpn.api.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.orbvpn.api.reposiitory.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
  securedEnabled = true,
  jsr250Enabled = true,
  prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final JwtTokenFilter jwtTokenFilter;

  @Value("${application.website-url}")
  private String websiteUrl;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .cors().configurationSource(corsConfig()).and()
      .authorizeRequests()
      .anyRequest().permitAll()
      .and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .csrf().disable()
      .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
  }

  // Expose authentication manager bean
  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  private CorsConfigurationSource corsConfig() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(websiteUrl));
    corsConfiguration
      .setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
    corsConfiguration.setAllowCredentials(Boolean.TRUE);
    corsConfiguration
      .setExposedHeaders(List.of("Content-Disposition", "Location", "Authorization"));
    corsConfiguration.setAllowedHeaders(
      List.of("Authorization", "Access-Control-Allow-Origin", "Content-Type", "Cache-Control",
        "X-Requested-With"));
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

}
