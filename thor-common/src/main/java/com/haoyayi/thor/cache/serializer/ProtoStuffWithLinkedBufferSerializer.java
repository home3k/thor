/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.cache.serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.haoyayi.thor.cache.measure.Ram;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ProtoStuffWithLinkedBufferSerializer implements Serializer{

	static int bufferSize = Ram.Kb(3);

	private static final ThreadLocal<LinkedBuffer> localBuffer = new ThreadLocal<LinkedBuffer>() {
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(bufferSize);
		}
	};

	public ProtoStuffWithLinkedBufferSerializer() {

	}

	public ProtoStuffWithLinkedBufferSerializer(int bufferSize) {
		ProtoStuffWithLinkedBufferSerializer.bufferSize = bufferSize;
	}

	@SuppressWarnings("unchecked")
	public byte[] serialize(Object obj, Class clazz) {
		Schema schema = RuntimeSchema.getSchema(clazz);
		final LinkedBuffer buffer = localBuffer.get();
		byte[] protostuff = null;

		try {
			protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} finally {
			buffer.clear();
		}
		return protostuff;
	}

	@SuppressWarnings("unchecked")
	public Object deserialize(byte[] source, Class<?> clazz) {
		Object object;
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("deserialize exception", e);
		}
		Schema schema = RuntimeSchema.getSchema(clazz);
		ProtostuffIOUtil.mergeFrom(source, object, schema);
		return object;
	}
}
