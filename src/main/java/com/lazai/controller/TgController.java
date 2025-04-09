package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.MethodTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Controller;
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

@Controller
@RequestMapping("/tg")
public class TgController {

    @GetMapping("/authHtml")
    @ResultLog(name = "TgController.authHtml", methodType = MethodTypeEnum.UPPER)
    public String authHtml(HttpServletRequest request){
        return "tgLoginCallback";
    }

}
