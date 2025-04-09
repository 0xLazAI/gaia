package com.lazai.enums;

public enum TaskTemplateStatus {

    ACTIVE("ACTIVE", ""),
    IN_ACTIVE("IN_ACTIVE", ""),
    ;

    private final String value;
    private final String desc;

    TaskTemplateStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static TaskTemplateStatus getByValue(String value){
        for(TaskTemplateStatus result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static TaskTemplateStatus getByDesc(String desc){
        for(TaskTemplateStatus result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }

}
