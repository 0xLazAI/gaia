package com.lazai.enums;


public enum UserStatusEnum {
    ACTIVE("ACTIVE", ""),
    IN_ACTIVE("IN_ACTIVE", ""),
    ;

    private final String value;
    private final String desc;

    UserStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static UserStatusEnum getByValue(String value){
        for(UserStatusEnum result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static UserStatusEnum getByDesc(String desc){
        for(UserStatusEnum result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }

}
