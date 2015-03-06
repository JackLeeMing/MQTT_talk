package com.ljk.mytest.manager;

import java.util.Stack;

/**
 * manager抽象基类
 * 用于管理派生的manager
 * 防止内存泄露，信息不一致等问题
 */
public abstract class BaseManager {
	
	private static Stack<BaseManager> mManagerStack = new Stack<BaseManager>();
	
	/** 默认调用该构造函数 */
	public BaseManager() {
		mManagerStack.add(this);
	}
	
	public abstract void destroy();
	
	public static void destroyAllManager() {
		for (BaseManager manager : mManagerStack) {
			manager.destroy();
			manager = null;
		}
	}
	
}
