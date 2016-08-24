/*
 * Copyright 2014-2020 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.validate;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface AuthPolicy {

    boolean auth(ProceedingJoinPoint pjp);

}
