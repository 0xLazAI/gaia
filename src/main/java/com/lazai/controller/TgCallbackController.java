package com.lazai.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TgService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.entity.User;
import com.lazai.repostories.UserRepository;
import com.lazai.utils.JWTUtils;
import com.lazai.utils.JsonUtils;
import com.lazai.utils.TgValidatorUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${tg.bot.token}")
    private String tgBotToken;

    @GetMapping("/auth")
    public String auth(@RequestParam Map<String, String> params) {
        String ethAddress = params.get("ethAddress");
        params.remove("ethAddress");
        if (TgValidatorUtils.isTelegramAuthValid(new HashMap<>(params), tgBotToken)) {
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


}
