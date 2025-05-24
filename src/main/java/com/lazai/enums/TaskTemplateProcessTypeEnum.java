package com.lazai.enums;

public enum TaskTemplateProcessTypeEnum {

    ONCE("ONCE", ""),
    DAILY("DAILY", ""),
    DAILY_TIMES("DAILY_TIMES", ""),
    COMMON("COMMON", ""),
    ;

    private final String value;
    private final String desc;

    TaskTemplateProcessTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static TaskTemplateProcessTypeEnum getByValue(String value){
        for(TaskTemplateProcessTypeEnum result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static TaskTemplateProcessTypeEnum getByDesc(String desc){
        for(TaskTemplateProcessTypeEnum result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }

}
