package ch.admin.bazg.prezius.reporting.adapter.in.frontendconfig;

import ch.admin.bit.jeap.starter.application.web.FrontendRouteRedirectExceptionHandler;
import org.springframework.boot.web.error.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.servlet.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class FrontendWebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("http://localhost:4206")
                .allowCredentials(false);
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/notFound").setViewName("forward:/index.html");
    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notFound"));
    }

    @Bean
    public FrontendRouteRedirectExceptionHandler frontendRouteRedirectExceptionHandler() {
        return new FrontendRouteRedirectExceptionHandler();
    }
}
