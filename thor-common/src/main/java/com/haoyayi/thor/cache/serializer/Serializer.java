/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.cache.serializer;


/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface Serializer {

	public byte[] serialize(Object obj, Class<?> clazz) ;

	public Object deserialize(byte[] source, Class<?> clazz);

}
