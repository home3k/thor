/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.cache.serializer;

import java.io.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class StandardSerializer implements Serializer {

	public byte[] serialize(Object obj, Class<?> clazz) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e) {
			throw new SerializerException(
					"object serialize exception, with object " + obj, e);
		} finally {
			if (null != oos)
				try {
					oos.close();
				} catch (IOException e) {
					throw new SerializerException(
							"object serialize exception, with object " + obj, e);
				}
		}
		return baos.toByteArray();
	}

	public Serializable deserialize(byte[] source, Class<?> clazz) {
		ByteArrayInputStream bis = new ByteArrayInputStream(source);
		Serializable obj;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			obj = (Serializable) ois.readObject();
		} catch (Exception e) {
			throw new SerializerException(
					"object serialize exception, with class " + clazz, e);
		} finally {
			if (null != ois)
				try {
					ois.close();
				} catch (IOException e) {
					throw new SerializerException(
							"object serialize exception, with class " + clazz, e);
				}
		}
		return obj;
	}
}
