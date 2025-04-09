package com.lazai.repostories.impl;

import com.lazai.entity.TgJoinGroupMembers;
import com.lazai.entity.dto.TgJoinGroupMembersQueryParam;
import com.lazai.mapper.TgJoinGroupMembersMapper;
import com.lazai.repostories.TgJoinGroupMembersRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TgJoinGroupMembersRepositoryImpl implements TgJoinGroupMembersRepository {

    @Autowired
    private TgJoinGroupMembersMapper tgJoinGroupMembersMapper;

    public void insert(TgJoinGroupMembers tgJoinGroupMembers){
        if (tgJoinGroupMembers.getCreatedAt() == null) {
            tgJoinGroupMembers.setCreatedAt(new Date());
        }
        if (tgJoinGroupMembers.getUpdatedAt() == null) {
            tgJoinGroupMembers.setUpdatedAt(new Date());
        }
        if(StringUtils.isBlank(tgJoinGroupMembers.getStatus())){
            tgJoinGroupMembers.setStatus("init");
        }
        tgJoinGroupMembersMapper.insert(tgJoinGroupMembers);
    }

    public Integer updateById(TgJoinGroupMembers tgJoinGroupMembers){
        tgJoinGroupMembers.setUpdatedAt(new Date());
        return tgJoinGroupMembersMapper.updateById(tgJoinGroupMembers);
    }

    public List<TgJoinGroupMembers> queryList(TgJoinGroupMembersQueryParam param){
        return tgJoinGroupMembersMapper.queryList(param);
    }

}
