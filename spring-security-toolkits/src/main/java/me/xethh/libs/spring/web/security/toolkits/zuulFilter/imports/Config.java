package me.xethh.libs.spring.web.security.toolkits.zuulFilter.imports;

import me.xethh.libs.spring.web.security.toolkits.frontFilter.configurationProperties.FirstFilterProperties;
import me.xethh.libs.spring.web.security.toolkits.zuulFilter.DefaultRouteAuthenticationSetter;
import me.xethh.libs.spring.web.security.toolkits.zuulFilter.RouteAuthenticationSetter;
import me.xethh.libs.spring.web.security.toolkits.zuulFilter.ZuulPostFilter;
import me.xethh.libs.spring.web.security.toolkits.zuulFilter.ZuulPreFilter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static me.xethh.libs.spring.web.security.toolkits.frontFilter.FirstFilter.DEFAULT_LOGGER_ACCESS;
import static me.xethh.libs.spring.web.security.toolkits.frontFilter.FirstFilter.DEFAULT_LOGGER_RAW;

@EnableConfigurationProperties(FirstFilterProperties.class)
public class Config {
    @Autowired
    private FirstFilterProperties firstFilterProperties;
    @Bean
    public ZuulPreFilter zuulPreFilter(
    ){
        ZuulPreFilter zuulPreFilter = new ZuulPreFilter();
        return zuulPreFilter;
    }
    @Bean
    public ZuulPostFilter zuulPostFilter(
    ){
        ZuulPostFilter zuulPostFilter =  new ZuulPostFilter();
        return zuulPostFilter;
    }

    @Bean
    public RouteAuthenticationSetter routeAuthenticationSetter(
            @Value("${first-filter.zuul.with-authen.authen-type:None}") FirstFilterProperties.AuthenType authenType,
            @Value("${first-filter.zuul.with-authen.value:}") String configValue
    ){
        if(!authenType.equals(FirstFilterProperties.AuthenType.None) &&
                (configValue==null || configValue.equals(""))
        ){
            throw new RuntimeException("Route authentication setter cannot be initiated with empty value");
        }
        return new DefaultRouteAuthenticationSetter();
    }
}
