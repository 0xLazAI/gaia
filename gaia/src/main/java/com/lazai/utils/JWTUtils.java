package com.lazai.utils;

import com.alibaba.fastjson.JSON;
import com.lazai.entity.User;
import com.lazai.enums.constant.CacheConstant;
import com.lazai.exception.DomainException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yozora
 * Description JSON Web Token
 **/
@Slf4j
public class JWTUtils {

    private static final String SECRET_KEY = "MHcCAQEEIKTna+5lOYrjhhp3xth58Ef3WosBb/haSpjj/VvQszNaoAoGCCqGSM49AwEHoUQDQgAE2DhX3HIW9uv8IOX40PpKDw2945ZVfUMif5Q6/Sj9doreq2kLcqVwomz8nKFKXxRtEUCR6zdbyl9xvEuBRmO4TQ==";


    private JWTUtils() throws ClassNotFoundException {
        throw new ClassNotFoundException("can not instantiate JWTUtils.");
    }

    public static String createToken(Map<String, Object> claims, long ttlMillis) {
        long tokenExpireSeconds = 900L;
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setIssuedAt(now).setClaims(claims).signWith(signatureAlgorithm, SECRET_KEY);
        long expMillis;
        if (ttlMillis > 0L) {
            expMillis = nowMillis + ttlMillis;
        } else {
            expMillis = nowMillis + tokenExpireSeconds * 1000L;
        }

        Date exp = new Date(expMillis);
        builder.setExpiration(exp);
        String token = builder.compact();

        return Base64.getEncoder().encodeToString(token.getBytes());
    }

    public static String createToken(User userInfo, long ttlMillis) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("currentTimestamp", System.currentTimeMillis());
        claims.put("userInfo", userInfo);
        String token = createToken(claims, ttlMillis * 1000);
        // 存储 access token
        RedisUtils.set(CacheConstant.ACCESS_TOKEN_U_PREFIX + token, JSON.toJSONString(userInfo), ttlMillis);
        RedisUtils.set(CacheConstant.USER_TO_TOKEN + userInfo.getId(), token, ttlMillis);
        return token;
    }

    public static String getTokenByUserId(String userId){
        return RedisUtils.get(CacheConstant.USER_TO_TOKEN + userId);
    }

    public static User getUser() throws DomainException {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        String token = request != null ? request.getHeader(HttpHeaders.AUTHORIZATION) : null;
        assert token != null;
        token = token.replace("Bearer ", "");
        String s = RedisUtils.get(CacheConstant.ACCESS_TOKEN_U_PREFIX + token);
        if (StringUtils.isBlank(s)) {
            throw new DomainException("not login!", 401);
        }
        return JSON.parseObject(s, User.class);
    }

    public static User getUserUnsafe() throws DomainException {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        String token = request != null ? request.getHeader(HttpHeaders.AUTHORIZATION) : null;
        assert token != null;
        token = token.replace("Bearer ", "");
        String s = RedisUtils.get(CacheConstant.ACCESS_TOKEN_U_PREFIX + token);
        return  JSON.parseObject(s, User.class);
    }

    /**
     * create timestamp token
     *
     * @return java.lang.String
     * @author yozora
     */
    public static String createToken(long ttlMillis) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("currentTimestamp", System.currentTimeMillis());
        return createToken(claims, ttlMillis);
    }

    /**
     * get token from request header
     *
     * @return java.lang.String
     * @author yozora
     */
    public static String getToken() {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        return request != null ? request.getHeader(HttpHeaders.AUTHORIZATION) : null;
    }

    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(new String(Base64.getDecoder().decode(token))).getBody();
        } catch (Exception e) {
            throw new DomainException("claim token failed", 403);
        }
    }

    public static User getUserFromHeaderToken(){
        String token = getToken();
        Claims claims = getClaimsFromToken(token);
        if(claims.containsKey("userInfo")){
            return claims.get("userInfo", User.class);
        }
        return null;
    }

    /**
     * validate token
     *
     * @param token token
     * @return java.lang.Boolean
     * @author yozora
     */
    @Deprecated
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            return expiration.before(now);
        } catch (Exception e) {
            log.error("Token expired!\n{}", e.getMessage());
            return true;
        }
    }

    /**
     * refresh token
     *
     * @param token     token
     * @param ttlMillis expire time
     * @return java.lang.String
     * @author yozora
     */
    @Deprecated
    public static String refreshToken(String token, long ttlMillis) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("refresh", new Date());
            refreshedToken = createToken(claims, ttlMillis);
        } catch (Exception var5) {
            refreshedToken = null;
        }

        return refreshedToken;
    }

    /**
     * description: generate UUID
     *
     * @return java.lang.String
     * @author yozora
     **/
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateHash(String str) {
        str = str + System.currentTimeMillis() + new Random().nextInt(Integer.MAX_VALUE);
        return Hash.sha3(Numeric.toHexStringNoPrefix((str).getBytes(StandardCharsets.UTF_8))).substring(0, 11);
    }

}
