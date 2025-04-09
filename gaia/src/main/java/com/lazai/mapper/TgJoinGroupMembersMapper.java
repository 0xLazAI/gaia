package com.lazai.mapper;

import com.lazai.entity.TgJoinGroupMembers;
import com.lazai.entity.dto.TgJoinGroupMembersQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TgJoinGroupMembersMapper {

    Integer insert(TgJoinGroupMembers tgJoinGroupMembers);

    Integer updateById(TgJoinGroupMembers tgJoinGroupMembers);

    List<TgJoinGroupMembers> queryList(TgJoinGroupMembersQueryParam tgJoinGroupMembersQueryParam);


}
