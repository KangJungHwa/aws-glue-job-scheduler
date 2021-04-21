package com.lgdisplay.bigdata.api.glue.scheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final String baseUrl;

    public WebMvcConfiguration(@Value("${springfox.documentation.swagger-ui.base-url:}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');

        // Swagger UI를 표시하기 위한 리소스를 설정한다.
        registry.addResourceHandler(baseUrl + "/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /로 접근하면 Swagger UI로 리다이렉트한다.
        registry.addRedirectViewController("/", "/swagger-ui/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // Internal View Resolver가 추가되어 있지 않으면 Swagger 화면이 표시되지 않는다.
        registry.viewResolver(new InternalResourceViewResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration registration = registry.addMapping("/**");
        registration.allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }
}