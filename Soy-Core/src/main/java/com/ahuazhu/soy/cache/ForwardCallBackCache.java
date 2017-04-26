package com.ahuazhu.soy.cache;

import com.ahuazhu.soy.forward.ForwardCallBack;
import com.ahuazhu.soy.model.QueryKey;
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
public class ForwardCallBackCache implements Cache<QueryKey, ForwardCallBack> {

    private org.ehcache.Cache<QueryKey, ForwardCallBack> ehcache;

    public ForwardCallBackCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("dns",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(QueryKey.class, ForwardCallBack.class,
                                ResourcePoolsBuilder.heap(10000))
                                .withExpiry(Expirations.timeToLiveExpiration(
                                                Duration.of(Constants.SYSTEM.CACHE.EXPIRE_MILLIS, TimeUnit.MILLISECONDS)
                                        )
                                )
                                .build())
                .build(true);
        ehcache = cacheManager.getCache("dns", QueryKey.class, ForwardCallBack.class);
    }

    @Override
    public ForwardCallBack getValue(QueryKey key) {
        return ehcache.get(key);
    }

    @Override
    public void remove(QueryKey key) {
        ehcache.remove(key);
    }

    @Override
    public void putValue(QueryKey key, ForwardCallBack value) {
        ehcache.put(key, value);
    }

    public synchronized ForwardCallBack takeValue(QueryKey key)  {
        ForwardCallBack v = getValue(key);
        if (v != null) {
            remove(key);
        }
        return v;
    }
}
