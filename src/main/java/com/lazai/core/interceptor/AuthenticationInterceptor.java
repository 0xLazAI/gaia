package com.lazai.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.constant.CacheConstant;
import com.lazai.utils.RedisUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author yozora
 * Description permission verification interceptor
 **/
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * permission control excluded URL
     */
    @Value("${not-login-whitelist.urls}")
    private String EXCLUDE_URI;

    /**
     * permission verification
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return boolean
     * @author yozora
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        log.info("request uri: {}", request.getRequestURI());
        String[] exclude_uri = EXCLUDE_URI.split(",");
        boolean allMatch = Arrays.stream(exclude_uri).anyMatch(uri -> request.getRequestURI().startsWith(uri));
//        if (!allMatch) {
//            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
//            log.info("access url: {} token: {}", request.getRequestURI(), token);
//            if (token == null) {
//                log.info("token is null");
//                output(response, 0);
//                return false;
//            }
//            // access token
//            token = token.replace("Bearer ", "");
//            String userAddress = RedisUtils.get(CacheConstant.ACCESS_TOKEN_U_PREFIX + token);
//            if (userAddress == null) {
//                log.info("token user address is null");
//                output(response, 1);
//                return false;
//            }
//            response.setHeader(HttpHeaders.AUTHORIZATION, token);
//        }
        return true;
    }


    /**
     * output response
     *
     * @param response response
     * @param type     0-token is null 1-no permission
     * @author yozora
     */
    public void output(HttpServletResponse response, int type) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = null;
        JsonApiResponse<String> apiResponse;
        if (type == 0) {
            apiResponse = JsonApiResponse.failed(null,401,"not login!");
        } else {
            apiResponse = JsonApiResponse.failed(null, 401, "not auth!");
        }
        try {
            outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(apiResponse).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("output error", e);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

}
