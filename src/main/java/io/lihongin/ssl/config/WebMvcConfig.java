package io.lihongin.ssl.config;

import io.lihongin.ssl.converter.SslHttpMessageConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private BeanFactory beanFactory;

    public WebMvcConfig(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new SslHttpMessageConverter(beanFactory.getBean(SslConfig.class)));
    }

}