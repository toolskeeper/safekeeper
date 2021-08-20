package cn.safekeeper.core.session;

import cn.safekeeper.common.function.SafeKeeperFunction;
import cn.safekeeper.common.utils.SafeKeeperUtils;
import cn.safekeeper.core.manager.SafeKeeperManager;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 会话
 */
@Setter
@Getter
public class SafeKeeperSession implements Serializable {
    /**
     * session id
     */
    private String id;
    /**
     * session 创建时间
     */
    private long createTime;

    /**
     * session 附带数据
     */
    private final Map<String, Object> data = new ConcurrentHashMap<>();

    /**
     * 此Session绑定的token签名列表
     */
    private final List<SafeKeeperToken> tokenList = new CopyOnWriteArrayList<>();

    /**
     * 构建一个Session对象
     */
    public SafeKeeperSession() {
    }

    /**
     * 构建一个Session对象
     * @param id Session的id
     */
    public SafeKeeperSession(String id) {
        this.id = id;
        this.createTime = System.currentTimeMillis();
        SafeKeeperManager.getSafeKeeperTokenListener().doCreateSession(id);
    }

    /**
     * 查找一个token签名
     * @param tokenValue token值
     * @return 查找到的tokenSign
     */
    public SafeKeeperToken getTokenSign(String tokenValue) {
        for (SafeKeeperToken safeKeeperToken : getTokenList()) {
            if (safeKeeperToken.getValue().equals(tokenValue)) {
                return safeKeeperToken;
            }
        }
        return null;
    }

    /**
     * 添加一个token签名
     * @param safeKeeperToken token签名
     */
    public void addTokenSign(SafeKeeperToken safeKeeperToken) {
        // 如果已经存在于列表中，则无需再次添加
        for (SafeKeeperToken safeKeeperToken2 : getTokenList()) {
            if (safeKeeperToken2.getValue().equals(safeKeeperToken.getValue())) {
                return;
            }
        }
        // 添加并更新
        getTokenList().add(safeKeeperToken);
        update();
    }

    /**
     * 移除一个token签名
     * @param tokenValue token名称
     */
    public void removeTokenSign(String tokenValue) {
        SafeKeeperToken safeKeeperToken = getTokenSign(tokenValue);
        if (getTokenList().remove(safeKeeperToken)) {
            update();
        }
    }

    /**
     * 更新会话
     */
    public void update() {
        //调用底层持久化进行更新
        SafeKeeperManager.getSafeKeeperTokenRealm().updateSession(this);
    }


    /**
     * 注销，删除会话
     */
    public void logout() {
        SafeKeeperManager.getSafeKeeperTokenRealm().deleteSession(this.id);
        SafeKeeperManager.getSafeKeeperTokenListener().doLogoutSession(id);
    }

    /**
     * 获取此Session的剩余存活时间 (单位: 秒)
     * @return 此Session的剩余存活时间 (单位: 秒)
     */
    public long getTimeout() {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getSessionTimeout(this.id);
    }

    /**
     * 修改此Session的剩余存活时间
     * @param timeout 过期时间 (单位: 秒)
     */
    public void updateTimeout(long timeout) {
        SafeKeeperManager.getSafeKeeperTokenRealm().updateSessionTimeout(this.id, timeout);
    }

    /**
     * 修改此Session的最小剩余存活时间 (只有在Session的过期时间低于指定的minTimeout时才会进行修改)
     * @param minTimeout 过期时间 (单位: 秒)
     */
    public void updateMinTimeout(long minTimeout) {
        if(getTimeout() < minTimeout) {
            SafeKeeperManager.getSafeKeeperTokenRealm().updateSessionTimeout(this.id, minTimeout);
        }
    }

    /**
     * 修改此Session的最大剩余存活时间 (只有在Session的过期时间高于指定的maxTimeout时才会进行修改)
     * @param maxTimeout 过期时间 (单位: 秒)
     */
    public void updateMaxTimeout(long maxTimeout) {
        if(getTimeout() > maxTimeout) {
            SafeKeeperManager.getSafeKeeperTokenRealm().updateSessionTimeout(this.id, maxTimeout);
        }
    }
    /**
     * 取值
     * @param key key
     * @return 值
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     *
     * 取值 (指定默认值)
     * @param <T> 默认值的类型
     * @param key key
     * @param defaultValue 取不到值时返回的默认值
     * @return 值
     */
    public <T> T get(String key, T defaultValue) {
        return getValueByDefaultValue(get(key), defaultValue);
    }

    /**
     *
     * 取值 (如果值为null，则执行fun函数获取值)
     * @param <T> 返回值的类型
     * @param key key
     * @param fun 值为null时执行的函数
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, SafeKeeperFunction fun) {
        Object value = get(key);
        if(value == null) {
//            value = fun.run();
            set(key, value);
        }
        return (T) value;
    }

    /**
     * 取值 (转String类型)
     * @param key key
     * @return 值
     */
    public String getString(String key) {
        Object value = get(key);
        if(value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    /**
     * 取值 (转int类型)
     * @param key key
     * @return 值
     */
    public int getInt(String key) {
        return getValueByDefaultValue(get(key), 0);
    }

    /**
     * 取值 (转long类型)
     * @param key key
     * @return 值
     */
    public long getLong(String key) {
        return getValueByDefaultValue(get(key), 0L);
    }

    /**
     * 取值 (转double类型)
     * @param key key
     * @return 值
     */
    public double getDouble(String key) {
        return getValueByDefaultValue(get(key), 0.0);
    }

    /**
     * 取值 (转float类型)
     * @param key key
     * @return 值
     */
    public float getFloat(String key) {
        return getValueByDefaultValue(get(key), 0.0f);
    }

    /**
     * 取值 (指定转换类型)
     * @param <T> 泛型
     * @param key key
     * @param cs 指定转换类型
     * @return 值
     */
    public <T> T getModel(String key, Class<T> cs) {
        return SafeKeeperUtils.getValueByType(get(key), cs);
    }

    /**
     * 取值 (指定转换类型, 并指定值为Null时返回的默认值)
     * @param <T> 泛型
     * @param key key
     * @param cs 指定转换类型
     * @param defaultValue 值为Null时返回的默认值
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getModel(String key, Class<T> cs, Object defaultValue) {
        Object value = get(key);
        if(valueIsNull(value)) {
            return (T)defaultValue;
        }
        return SafeKeeperUtils.getValueByType(value, cs);
    }

    /**
     * 返回当前Session的所有key
     *
     * @return 所有值的key列表
     */
    public Set<String> keys() {
        return data.keySet();
    }

    /**
     * 写值
     * @param key   名称
     * @param value 值
     * @return 对象自身
     */
    public SafeKeeperSession set(String key, Object value) {
        data.put(key, value);
        update();
        return this;
    }

    /**
     * 写值(只有在此key原本无值的时候才会写入)
     * @param key   名称
     * @param value 值
     * @return 对象自身
     */
    public SafeKeeperSession setDefaultValue(String key, Object value) {
        if(has(key) == false) {
            data.put(key, value);
            update();
        }
        return this;
    }

    /**
     * 是否含有某个key
     * @param key has
     * @return 是否含有
     */
    public boolean has(String key) {
        return !valueIsNull(get(key));
    }

    /**
     * 删值
     * @param key 要删除的key
     * @return 对象自身
     */
    public SafeKeeperSession delete(String key) {
        data.remove(key);
        update();
        return this;
    }

    /**
     * 清空所有值
     */
    public void clear() {
        data.clear();
        update();
    }

    /**
     * 获取数据挂载集合（如果更新map里的值，请调用session.update()方法避免产生脏数据 ）
     *
     * @return 返回底层储存值的map对象
     */
    public Map<String, Object> getDataMap() {
        return data;
    }

    /**
     * 写入数据集合 (不改变底层对象，只将此dataMap所有数据进行替换)
     * @param dataMap 数据集合
     */
    public void refreshDataMap(Map<String, Object> dataMap) {
        this.data.clear();
        this.data.putAll(dataMap);
        this.update();
    }


    // --------- 工具方法

    /**
     * 判断一个值是否为null
     * @param value 指定值
     * @return 此value是否为null
     */
    public boolean valueIsNull(Object value) {
        return value == null || value.equals("");
    }

    /**
     * 根据默认值来获取值
     * @param <T> 泛型
     * @param value 值
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    protected <T> T getValueByDefaultValue(Object value, T defaultValue) {
        if(valueIsNull(value)) {
            return (T)defaultValue;
        }
        Class<T> cs = (Class<T>) defaultValue.getClass();
        return SafeKeeperUtils.getValueByType(value, cs);
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.set(key) </h1>
     * 写入一个值
     *
     * @param key   名称
     * @param value 值
     */
    @Deprecated
    public void setAttribute(String key, Object value) {
        data.put(key, value);
        update();
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.get(key) </h1>
     * 取出一个值
     *
     * @param key 名称
     * @return 值
     */
    @Deprecated
    public Object getAttribute(String key) {
        return data.get(key);
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.get(key, defaultValue) </h1>
     * 取值，并指定取不到值时的默认值
     * @param key          名称
     * @param defaultValue 取不到值的时候返回的默认值
     * @return value
     */
    @Deprecated
    public Object getAttribute(String key, Object defaultValue) {
        Object value = getAttribute(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.delete(key) </h1>
     * 移除一个值
     *
     * @param key 要移除的值的名字
     */
    @Deprecated
    public void removeAttribute(String key) {
        data.remove(key);
        update();
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.clear() </h1>
     * 清空所有值
     */
    @Deprecated
    public void clearAttribute() {
        data.clear();
        update();
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.has(key) </h1>
     * 是否含有指定key
     *
     * @param key 是否含有指定值
     * @return 是否含有
     */
    @Deprecated
    public boolean containsAttribute(String key) {
        return data.containsKey(key);
    }

    /**
     * <h1> 此函数设计已过时，未来版本可能移除此类，请及时更换为: session.keys() </h1>
     * 返回当前session会话所有key
     *
     * @return 所有值的key列表
     */
    @Deprecated
    public Set<String> attributeKeys() {
        return data.keySet();
    }

}
