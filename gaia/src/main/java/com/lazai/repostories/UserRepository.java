package com.lazai.repostories;

import com.lazai.entity.User;

public interface UserRepository {

    User findById(String id, Boolean isLock);

    User findByEthAddress(String ethAddress, Boolean isLock);

    User findByXId(String xId);

    User findByTgId(String tgId);

    String insert(User user);

    Integer updateById(User user);

    Integer updateByEthAddress(User user);
}
