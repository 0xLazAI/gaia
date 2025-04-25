package com.lazai.enums;

public enum TaskActionStatusEnum {

    PROCESSING("processing", ""),
    FINISH("finish", ""),
    INIT("init", ""),
    ERROR("error", ""),
    ;

    private final String value;
    private final String desc;

    TaskActionStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static TaskActionStatusEnum getByValue(String value){
        for(TaskActionStatusEnum result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static TaskActionStatusEnum getByDesc(String desc){
        for(TaskActionStatusEnum result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }

}
