package com.ahuazhu.soy.cache;

import com.ahuazhu.soy.forward.AnswerHandler;
import com.ahuazhu.soy.forward.ForwardCallBack;
import com.ahuazhu.soy.modal.QueryKey;
import com.ahuazhu.soy.utils.Constants;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhengwenzhu on 2017/4/7.
 */
public class UdpCallBackCache implements Cache<QueryKey, AnswerHandler> {

    private org.ehcache.Cache<QueryKey, AnswerHandler> ehcache;

    public UdpCallBackCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("UdpCallBackCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(QueryKey.class, AnswerHandler.class,
                                ResourcePoolsBuilder.heap(10000))
                                .withExpiry(Expirations.timeToLiveExpiration(
                                                Duration.of(Constants.SYSTEM.CACHE.EXPIRE_MILLIS, TimeUnit.MILLISECONDS)
                                        )
                                )
                                .build())
                .build(true);
        ehcache = cacheManager.getCache("UdpCallBackCache", QueryKey.class, AnswerHandler.class);
    }

    @Override
    public AnswerHandler getValue(QueryKey key) {
        return ehcache.get(key);
    }

    @Override
    public void remove(QueryKey key) {
        ehcache.remove(key);
    }

    @Override
    public void putValue(QueryKey key, AnswerHandler value) {
        ehcache.put(key, value);
    }
}
