package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.QueryKey;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class CacheProcessor implements Processor {
    Cache<QueryKey, Message> cache;

    public CacheProcessor() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("dns",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(QueryKey.class, Message.class,
                                ResourcePoolsBuilder.heap(100))
                                .build())
                .build(true);
        cache = cacheManager.getCache("dns", QueryKey.class, Message.class);
    }

    @Override
    public void process(RequestContext request, ResponseContext response, ProcessorChain chain) throws SoyException {

    }

    public static void main(String[] args) {
        CacheProcessor cacheProcessor = new CacheProcessor();
        cacheProcessor.cache.put(new QueryKey("123"), new Message(123));
        cacheProcessor.cache.put(new QueryKey("321"), new Message(321));

        System.out.println(cacheProcessor.cache.get(new QueryKey("123")));
        System.out.println(cacheProcessor.cache.get(new QueryKey("321")));

    }
}
