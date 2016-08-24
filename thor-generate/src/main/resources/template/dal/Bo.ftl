/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.dal.bo;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.haoyayi.thor.dal.base.AbstractBo;
import com.haoyayi.thor.dal.base.FieldQualifier;

/**
 * This class is automatically generated, Unless special scene, DO NOT modify!!
 */
public class ${Classname} extends AbstractBo {

	${setStr}

    public Long getPkid() {
        return this.${pk};
    }

    public void setPkid(Long id) {
        this.${pk} = id;
    }
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
