package com.lazai.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TgService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.entity.User;
import com.lazai.repostories.UserRepository;
import com.lazai.utils.JWTUtils;
import com.lazai.utils.JsonUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/tgCallback")
public class TgCallbackController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TgService tgService;

    @GetMapping("/auth")
    public String auth(@RequestParam Map<String, String> params) {
        String ethAddress = params.get("ethAddress");
        params.remove("ethAddress");
        if (isTelegramAuthValid(new HashMap<>(params), "7630739495:AAFkS-EIvTZMiwQB7ZAa3LsYibQLx7Jh1r0")) {
            // 登录成功
            User user = userRepository.findByEthAddress(ethAddress, false);
            if(user != null){
                user.setTgId(params.get("id"));
                String content = user.getContent();
                JSONObject contentObj = JSON.parseObject(content);
                contentObj.put("tgUserInfo", JSONObject.toJSONString(params));
                user.setContent(JSON.toJSONString(contentObj));
                userRepository.updateById(user);
            }else {
                user = new User();
                user.setStatus("active");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tgUserInfo", JSONObject.toJSONString(params));
                user.setContent(JSON.toJSONString(jsonObject));
                user.setName(params.get("username"));
                user.setTgId(params.get("id"));
                user.setEthAddress(ethAddress);
                userRepository.insert(user);
            }
            return "Login Success! Welcome " + params.get("username");
        } else {
            return "Invalid login attempt.";
        }
    }

    @GetMapping("/callbackTest")
    public JsonApiResponse<Object> callbackTest(@RequestParam Map<String, String> params) {
        JSONObject rt = tgService.getUpdates();
        return JsonApiResponse.success(rt);
    }

    @GetMapping("/scheduleTest")
    public JsonApiResponse<Object> scheduleTest(@RequestParam Map<String, String> params) {
        tgService.handleGroupNewMembersTask();
        return JsonApiResponse.success(true);
    }

    private boolean isTelegramAuthValid(Map<String, String> data, String botToken) {
        String checkHash = data.get("hash");
        data.remove("hash");

        List<String> fields = data.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .sorted()
                .collect(Collectors.toList());

        String dataCheckString = String.join("\n", fields);

        try {
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey, "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hashBytes = sha256_HMAC.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String generatedHash = Hex.encodeHexString(hashBytes);

            return generatedHash.equalsIgnoreCase(checkHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
