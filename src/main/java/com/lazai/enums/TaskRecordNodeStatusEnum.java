package com.lazai.enums;

public enum TaskRecordNodeStatusEnum {

    PROCESSING("processing", ""),
    FINISH("finish", ""),
    INIT("init", ""),

    ;

    private final String value;
    private final String desc;

    TaskRecordNodeStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static TaskRecordNodeStatusEnum getByValue(String value){
        for(TaskRecordNodeStatusEnum result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static TaskRecordNodeStatusEnum getByDesc(String desc){
        for(TaskRecordNodeStatusEnum result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }

}
