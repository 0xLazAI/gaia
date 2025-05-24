package com.lazai.mapper;

import com.lazai.entity.UserInvites;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserInvitesMapper {

    @Select("SELECT * FROM user_invites WHERE inviting_user = #{invitingUserId}")
    List<UserInvites> getByInvitingUserId(String invitingUserId);

    @Select("SELECT * FROM user_invites WHERE invited_user = #{invitedUserId}")
    List<UserInvites> getByInvitedUserId(String invitedUserId);

    Integer insert(UserInvites userInvites);

    Integer updateById(UserInvites userInvites);

}
