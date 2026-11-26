package ch.admin.bazg.prezius.reporting.adapter.in.frontendconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
class FrontendWebSecurityConfig {

    @Bean
    SecurityFilterChain frontendSecurityFilterChain(final HttpSecurity http) throws Exception {
        // protect the API
        // allow public access to frontend resources (i.e. non-/api-routes)
        // permit open access to open API docs & swagger ui as they are only enabled on test environments
        final RequestMatcher pathMatcher = PathPatternRequestMatcher.pathPattern("/*api/**");

        http.securityMatcher(new NegatedRequestMatcher(pathMatcher))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .anyRequest()
                        .permitAll())
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }
}
