package com.ljk.mytest.manager;

/** 
 * 需要生成数据库映射表<id, DAOClass>的类接口 
 */
public interface DAO<T> {
	/** 获取ID */
	public T getId();
}
