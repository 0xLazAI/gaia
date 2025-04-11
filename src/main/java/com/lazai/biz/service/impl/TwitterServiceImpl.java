package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TaskService;
import com.lazai.biz.service.TwitterService;
import com.lazai.biz.service.UserService;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.TaskRecord;
import com.lazai.entity.User;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.enums.TaskStatusEnum;
import com.lazai.exception.DomainException;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.request.BindTwitterUserInfoRequest;
import com.lazai.request.TaskCreateRequest;
import com.lazai.utils.JWTUtils;
import com.lazai.utils.RedisUtils;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.user.UserList;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import okhttp3.*;
import okhttp3.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.IDs;


import java.net.URI;
import java.net.URLEncoder;
import java.util.*;


@Service
public class TwitterServiceImpl implements TwitterService {

    @Autowired
    private Twitter twitter;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate twitterRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private  OkHttpClient okHttpClient;

    @Value("${clientId}")
    private String clientId;

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");


    private Set<Long> knownFollowerIds = new HashSet<>();

    public static String X_FOCUS_USER_PREFIX = "x_focus_user_";

    //@Scheduled(fixedRate = 60000) // 每1分钟检查一次
    public void checkFollowers() {
//        oauth.consumerKey=wyL8ysC4LMQpLGwNXTvFjAACp
//        oauth.consumerSecret=OvSfVnGCIHc1QLl0DRVMrYRrpH2aCyxJMpd69BcjlsgF9yl3Ve
//        oauth.accessToken=1707288505540579328-K9Q5em08NZW5Y3H6FknUVrQtrqCLE9
//        oauth.accessTokenSecret=KS0cen7GyHQTwxXl7qNVHSHeAWH5xn4ZUUz5J3zWpSrBz
        TwitterClient twitterClient = new TwitterClient(TwitterCredentials.builder()
                .accessToken("1707288505540579328-K9Q5em08NZW5Y3H6FknUVrQtrqCLE9")
                .accessTokenSecret("KS0cen7GyHQTwxXl7qNVHSHeAWH5xn4ZUUz5J3zWpSrBz")
                .apiKey("wyL8ysC4LMQpLGwNXTvFjAACp")
                .apiSecretKey("OvSfVnGCIHc1QLl0DRVMrYRrpH2aCyxJMpd69BcjlsgF9yl3Ve")
                .build());
        UserList userList = twitterClient.getFollowers("44196397");
        System.out.println(JSON.toJSONString(userList));

    }

    public Map<String, Object> getUserByUsername(String username) {
        String url = "https://api.twitter.com/2/users/by/username/" + username;
        return twitterRestTemplate.getForObject(url, Map.class);
    }

    public JSONObject getTokenByCode(String code){
        okhttp3.MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create("",mediaType);

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.x.com")
                .addPathSegment("2")
                .addPathSegment("oauth2")
                .addPathSegment("token")
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("grant_type", "authorization_code")
                .addQueryParameter("code_verifier", "challenge")
                .addQueryParameter("code", code)
                .addQueryParameter("redirect_uri", "http://localhost:8080/twitter/webhook/auth")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = null;
        JSONObject result = new JSONObject();
        try{
            response = okHttpClient.newCall(request).execute();
            if(response.code() != 200){
                String responseStr = response.body().string();
                throw new DomainException("http error",500);
            }
            String responseStr = response.body().string();
            return JSON.parseObject(responseStr);
        }catch (Throwable e){
            ERROR_LOGGER.error("httpError",e);
        }

        return result;
    }

    public JSONObject getMe(String token){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.x.com")
                .addPathSegment("2")
                .addPathSegment("users")
                .addPathSegment("me")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Response response = null;
        JSONObject result = new JSONObject();
        try{
            response = okHttpClient.newCall(request).execute();
            if(response.code() != 200){
                String responseStr = response.body().string();
                throw new DomainException("http error",500);
            }
            String responseStr = response.body().string();
            return JSON.parseObject(responseStr);
        }catch (Throwable e){
            ERROR_LOGGER.error("httpError",e);
        }

        return result.getJSONObject("data");
    }

    public Map<String, Object> getUserById(String id){
        String url = "https://api.twitter.com/2/users/" + id;
        return twitterRestTemplate.getForObject(url, Map.class);
    }

    public void newUserFocusTrigger(long id){
        //TODO 通过id获取screenName
        String screenName = "";
        User user = userService.findByXId(id+"");
        if(user != null){
            TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
            taskRecordQueryParam.setStatusList(Collections.singletonList(TaskStatusEnum.ACTIVE.value()));
            taskRecordQueryParam.setTaskTemplateId("twitterFocusLazAI");
            taskRecordQueryParam.setInnerUser(user.getId()+"");
            taskRecordQueryParam.setApp("gaia");
            List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
            if(CollectionUtils.isEmpty(taskRecords)){
                TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
                taskCreateRequest.setTemplateCode("twitterFocusLazAI");
                taskCreateRequest.setBizType("");
                taskCreateRequest.setBizId("");
                RoleDTO operatorRole = new RoleDTO();
                operatorRole.setId("0");
                taskCreateRequest.setOperator(operatorRole);
                String taskNo = taskService.createTask(taskCreateRequest);

                //TODO Trigger task
            }
        }
    }

    public void bindTwitterUserInfo(BindTwitterUserInfoRequest request){
        JSONObject tokenResult = getTokenByCode(request.getCode());
        JSONObject me = getMe(tokenResult.getString("access_token"));
        User user = JWTUtils.getUser();
        RedisUtils.set("twitter_bearer_token_" + user.getId(), tokenResult.getString("access_token"));
        user.setxId(me.getJSONObject("data").getString("id"));
        JSONObject userContent = JSON.parseObject(user.getContent());
        userContent.put("twitterUserInfo", me.getJSONObject("data"));
        user.setContent(JSON.toJSONString(userContent));
        userRepository.updateById(user);
    }

}
