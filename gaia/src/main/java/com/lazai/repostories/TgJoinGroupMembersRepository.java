package com.lazai.repostories;

import com.lazai.entity.TgJoinGroupMembers;
import com.lazai.entity.dto.TgJoinGroupMembersQueryParam;

import java.util.List;

public interface TgJoinGroupMembersRepository {

    void insert(TgJoinGroupMembers tgJoinGroupMembers);

    Integer updateById(TgJoinGroupMembers tgJoinGroupMembers);

    List<TgJoinGroupMembers> queryList(TgJoinGroupMembersQueryParam param);

}
