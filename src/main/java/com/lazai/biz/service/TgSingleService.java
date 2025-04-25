package com.lazai.biz.service;

/**
 * Tg biz service without other biz service
 */
public interface TgSingleService {
    /**
     * find user in specify group
     * @param tgId
     * @param botToken
     * @param groupId
     * @return
     */
    Boolean ifUserInGroup(String tgId, String botToken, String groupId);

}
