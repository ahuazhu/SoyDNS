package com.ahuazhu.soy.processor;

import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/3.
 */
public class CacheManager {

    private static Cache<String, Message> cache;

    public static Cache<String, Message> getCache() {
        if (cache == null) {
            synchronized (CacheManager.class) {
                if (cache == null) {
                    cache = createCache();
                }
            }
        }

        return cache;
    }

    private static Cache<String, Message> createCache() {
        org.ehcache.CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("dns",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Message.class,
                                ResourcePoolsBuilder.heap(1000))
                                .build())
                .build(true);
        return cacheManager.getCache("dns", String.class, Message.class);
    }
}
