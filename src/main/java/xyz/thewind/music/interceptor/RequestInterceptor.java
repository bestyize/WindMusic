package xyz.thewind.music.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
                if (null == accessLimit) {
                    return true;
                }
                int hours = accessLimit.hours();
                int maxCount = accessLimit.maxCount();
                String ip = request.getRemoteAddr();
                String servletPath = request.getServletPath();
                String key = ip + ":" + request.getContextPath() + ":" + servletPath;
                // 已经访问的次数
                String count = redisTemplate.opsForValue().get(key);
                if (null == count || -1 == Integer.parseInt(count)) {
                    redisTemplate.opsForValue().set(key, "1", hours, TimeUnit.HOURS);
                    return true;
                }
                if (Integer.parseInt(count) < maxCount) {
                    redisTemplate.opsForValue().increment(key, 1);
                    return true;
                }
                errResp(response);
                return false;
            }
        } catch (Exception ignored) {

        }
        return true;
    }

    /**
     * 拦截器异常响应
     *
     */
    public void errResp(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        // R.fail 是接口响应统一封装返回
        try {
            response.getWriter().println("{\"status\":-1,\"msg\":\"访问量过大，请12小时后重试\"}");
        } catch (IOException ignored) {

        }
    }


}
