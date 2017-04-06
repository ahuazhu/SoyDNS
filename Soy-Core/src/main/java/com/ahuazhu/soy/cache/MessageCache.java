package com.ahuazhu.soy.cache;

import com.ahuazhu.soy.modal.QuestionKey;
import com.ahuazhu.soy.utils.Constants;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.xbill.DNS.Message;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhengwenzhu on 2017/4/6.
 */
public class MessageCache implements Cache<QuestionKey, Message> {

    private org.ehcache.Cache<QuestionKey, Message> ehcache;

    public MessageCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("dns",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(QuestionKey.class, Message.class,
                                ResourcePoolsBuilder.heap(Constants.SYSTEM.CACHE.ENTITIES))
                                .withExpiry(Expirations.timeToLiveExpiration(
                                                Duration.of(Constants.SYSTEM.CACHE.EXPIRE_MILLIS, TimeUnit.MILLISECONDS)
                                        )
                                )
                                .build())
                .build(true);
        ehcache = cacheManager.getCache("dns", QuestionKey.class, Message.class);
    }

    @Override
    public Message getValue(QuestionKey key) {
        Message message = ehcache.get(key);
        if (message != null) {
            message = (Message) message.clone();
            message.getHeader().setID(key.getQuestionId());
        }
        return message;
    }

    @Override
    public void remove(QuestionKey key) {
        ehcache.remove(key);
    }

    @Override
    public void putValue(QuestionKey key, Message value) {
        ehcache.put(key, value);
    }
}
