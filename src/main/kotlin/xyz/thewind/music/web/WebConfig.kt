package xyz.thewind.music.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 前端是单页应用（frontend/ 构建后放在 static/），`/` 和 `/player?shareId=...` 都由 index.html 渲染。
 */
@Configuration
class WebConfig(private val accessLimitInterceptor: AccessLimitInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(accessLimitInterceptor).addPathPatterns("/api/**")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/player").setViewName("forward:/index.html")
    }
}
