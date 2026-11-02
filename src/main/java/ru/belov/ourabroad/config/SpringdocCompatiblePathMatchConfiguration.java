package ru.belov.ourabroad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Ensures MVC uses AntPathMatcher so springdoc Swagger UI resource mappings register successfully
 * (see spring.mvc.pathmatch.matching-strategy in application config).
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class SpringdocCompatiblePathMatchConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setPathMatcher(new AntPathMatcher());
    }
}
