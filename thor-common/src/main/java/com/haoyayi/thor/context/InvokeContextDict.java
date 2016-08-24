/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public enum InvokeContextDict {

    // 操作IP
    IP("ip"),

    // From
    OPTFROM("optfrom"),

    // From Module
    FROMMODULE("Source-Module"),

    // Source ip
    SOURCEIP("X-Read-IP"),

    LOGID("Logid"),

    TICKETID("Ticketid"),

    CHANNEL("Channel"),

    SOURCE("Source"),

    TAGS("Tags"),

    OPENID("openid"),

    OPTID("optid"),

    //获取sectionId
    SECTIONID("sectionid"),

    TOKEN("token"),

    LOGINROLE("loginRole"),
    
    LOGINNAME("loginName"),
    
    LOGINDENTISTID("loginDentistId"),
    
    UNIONID("unionId"),
    
    MERGERULE("mergeRule"),
    
    AUTORELATIONTAG("autoRelationTag"),
    
    VERSION("version");

    private final String value;

    public String getValue() {
        return value;
    }

    InvokeContextDict(String value) {
        this.value = value;
    }

}
