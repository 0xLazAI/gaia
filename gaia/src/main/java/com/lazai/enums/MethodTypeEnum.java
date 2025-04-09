package com.lazai.enums;

public enum MethodTypeEnum {

    UNKNOWN("1", "UNKNOWN"),
    UPPER("2", "UPPER"),
    DOWN("3", "DOWN"),
    DB("4", "DB"),
    INNER("5", "INNER");

    private final String value;
    private final String desc;

    MethodTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static MethodTypeEnum getByValue(String value){
        for(MethodTypeEnum result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static MethodTypeEnum getByDesc(String desc){
        for(MethodTypeEnum result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }
}
