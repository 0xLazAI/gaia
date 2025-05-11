package com.lazai.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TwitterService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.entity.User;
import com.lazai.repostories.UserRepository;
import com.lazai.utils.JWTUtils;
import com.lazai.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@RestController
@RequestMapping("/twitter/webhook")
public class TwitterWebhookController {

    private static final String CONSUMER_SECRET = "你的Consumer Secret";

    @Autowired
    private TwitterService twitterService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/auth")
    public void authCodeHandle(@RequestParam("state") String state, @RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject tokenResult = twitterService.getTokenByCode(code);
        List<String> stateList = List.of(state.split(","));
        JSONObject me = twitterService.getMe(tokenResult.getString("access_token"));
        User user = userRepository.findById(stateList.get(0), false);
        RedisUtils.set("twitter_bearer_token_" + user.getId(), tokenResult.getString("access_token"));
        user.setxId(me.getJSONObject("data").getString("id"));
        JSONObject userContent = JSON.parseObject(user.getContent());
        userContent.put("twitterUserInfo", me.getJSONObject("data"));
        user.setContent(JSON.toJSONString(userContent));
        userRepository.updateById(user);
        response.sendRedirect("https://t.me/CorruptedAlith_bot/mission?startapp=jump=mission");
    }

//    @GetMapping
//    public Map<String, String> crcCheck(@RequestParam("crc_token") String crcToken) {
//        String sha256Hash = generateCrcResponse(crcToken);
//        return Collections.singletonMap("response_token", "sha256=" + sha256Hash);
//    }
//
//
//    @PostMapping
//    public ResponseEntity<Void> handleEvent(@RequestBody Map<String, Object> payload) {
//        if (payload.containsKey("follow_events")) {
//            // 处理 follow 事件
//            List<Map<String, Object>> followEvents = (List<Map<String, Object>>) payload.get("follow_events");
//            for (Map<String, Object> event : followEvents) {
//                Map<String, Object> source = (Map<String, Object>) event.get("source");
//                String followerId = (String) source.get("id");
//                String screenName = (String) source.get("screen_name");
//                System.out.println("New follower: " + screenName);
//            }
//        }
//        return ResponseEntity.ok().build();
//    }

    private String generateCrcResponse(String token) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(CONSUMER_SECRET.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("CRC validation failed", e);
        }
    }

}
