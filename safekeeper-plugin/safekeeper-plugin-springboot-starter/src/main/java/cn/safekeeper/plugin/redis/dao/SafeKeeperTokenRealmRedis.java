package cn.safekeeper.plugin.redis.dao;

import cn.safekeeper.common.utils.SafeKeeperUtils;
import cn.safekeeper.core.manager.SafeKeeperTokenRealm;
import cn.safekeeper.core.session.SafeKeeperSession;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis持久化实现
 * @author skylark
 */
public class SafeKeeperTokenRealmRedis implements SafeKeeperTokenRealm {

    public SafeKeeperTokenRealmRedis(LettuceConnectionFactory safeKeeperLettuceConnectionFactory){
        // 指定相应的序列化方案
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
        // 构建StringRedisTemplate
        StringRedisTemplate stringTemplate = new StringRedisTemplate();
        stringTemplate.setConnectionFactory(safeKeeperLettuceConnectionFactory);
        stringTemplate.afterPropertiesSet();
        // 构建RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(safeKeeperLettuceConnectionFactory);
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        this.stringRedisTemplate = stringTemplate;
        this.objectRedisTemplate = template;
        this.isInit = true;
    }

    /**
     * String专用
     */
    public StringRedisTemplate stringRedisTemplate;

    /**
     * Object专用
     */
    public RedisTemplate<String, Object> objectRedisTemplate;

    /**
     * 标记：是否已初始化成功
     */
    public volatile boolean isInit;
    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        // 判断是否为永不过期
        if(timeout == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            stringRedisTemplate.opsForValue().set(key, value);
        } else {
            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if(expire == SafeKeeperTokenRealm.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if(timeout == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if(expire == SafeKeeperTokenRealm.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }


    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        return objectRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        // 判断是否为永不过期
        if(timeout == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            objectRedisTemplate.opsForValue().set(key, object);
        } else {
            objectRedisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if(expire == SafeKeeperTokenRealm.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        objectRedisTemplate.delete(key);
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        if(SafeKeeperUtils.isEmpty(key)){
            throw new NullPointerException("key的值为空");
        }else{
            return Optional.ofNullable(objectRedisTemplate.getExpire(key)).get();
        }
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if(timeout == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if(expire == SafeKeeperTokenRealm.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        objectRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public SafeKeeperSession getSession(String sessionId) {
        return (SafeKeeperSession) getObject(sessionId);
    }

    @Override
    public void setSession(SafeKeeperSession safeKeeperSession, long timeout) {
        setObject(safeKeeperSession.getId(), safeKeeperSession,timeout);
    }

    @Override
    public void updateSession(SafeKeeperSession safeKeeperSession) {
        updateObject(safeKeeperSession.getId(), safeKeeperSession);
    }

    @Override
    public void deleteSession(String sessionId) {
        deleteObject(sessionId);
    }

    @Override
    public long getSessionTimeout(String sessionId) {
        return getObjectTimeout(sessionId);
    }

    @Override
    public void updateSessionTimeout(String sessionId, long timeout) {
        updateTimeout(sessionId,timeout);
    }


    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        Set<String> keys = stringRedisTemplate.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<String>(keys);
        return SafeKeeperUtils.searchList(list, start, size);
    }
}
