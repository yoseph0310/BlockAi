package com.a506.blockai.config;

import com.a506.blockai.jwt.JwtAccessDeniedHandler;
import com.a506.blockai.jwt.JwtAuthenticationFilter;
import com.a506.blockai.jwt.JwtEntryPoint;
import com.a506.blockai.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**",
                        "/favicon.ico",
                        "/error",
                        "/swagger-resources/**",
                        "/swagger-ui.html/**",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/webjars/**"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//      .httpBasic().disable() // rest api ????????? ???????????? ????????????. ??????????????? ???????????? ???????????? ???????????? ??????????????? ??????.
        .csrf().disable() // rest api????????? csrf ????????? ?????????????????? disable??????.

        .exceptionHandling()
        .authenticationEntryPoint(jwtEntryPoint)
        .accessDeniedHandler(jwtAccessDeniedHandler)

        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token?????? ????????????????????? ???????????????????????? ????????????.

        .and()
        .authorizeRequests()
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .antMatchers("/api/users").permitAll()
        .antMatchers("/api/users/login").permitAll()
        .antMatchers("/api/users/sms").permitAll()
        .antMatchers("/api/users/phone/*").permitAll()
        .antMatchers("/api/users/*/log").permitAll()
        .antMatchers("/api/certification/users/*").permitAll()
        .anyRequest().authenticated()   // ????????? URI??? ??????????????? ?????? ??????

        .and()
        .cors()

        .and()
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // UsernamePasswordAuthenticationFilter ?????? JwtAuthenticationFilter ??????
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://coach85.p.ssafy.io");
        configuration.addAllowedOrigin("https://coach85.p.ssafy.io:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
