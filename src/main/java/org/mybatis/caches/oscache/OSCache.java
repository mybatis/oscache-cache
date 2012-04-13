/*
 *    Copyright 2010-2012 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.oscache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * The OSCache-based Cache implementation.
 *
 * @version $Id$
 */
public final class OSCache implements Cache {

    /**
     * The shared 
     */
    private static final GeneralCacheAdministrator CACHE_ADMINISTRATOR = new GeneralCacheAdministrator();

    /**
     * The {@code ReadWriteLock}.
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * This cache id.
     */
    private final String id;

    /**
     * Builds a new OSCache-based Cache.
     *
     * @param id the Mapper id.
     */
    public OSCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        CACHE_ADMINISTRATOR.flushGroup(this.id);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(Object key) {
        String keyString = key.toString();
        Object ret = null;

        try {
            ret = CACHE_ADMINISTRATOR.getFromCache(keyString);
        } catch (NeedsRefreshException e) {
            CACHE_ADMINISTRATOR.cancelUpdate(keyString);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    /**
     * {@inheritDoc}
     */
    public int getSize() {
        return CACHE_ADMINISTRATOR.getCache().getSize();
    }

    /**
     * {@inheritDoc}
     */
    public void putObject(Object key, Object value) {
        CACHE_ADMINISTRATOR.putInCache(key.toString(), value, new String[] { this.id });
    }

    /**
     * {@inheritDoc}
     */
    public Object removeObject(Object key) {
        String keyString = key.toString();
        Object ret = null;

        try {
            ret = CACHE_ADMINISTRATOR.getFromCache(keyString);
        } catch (NeedsRefreshException e) {
            CACHE_ADMINISTRATOR.cancelUpdate(keyString);
        }

        if (ret != null) {
            CACHE_ADMINISTRATOR.flushEntry(keyString);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Cache)) {
            return false;
        }

        Cache otherCache = (Cache) obj;
        return this.id.equals(otherCache.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "OSCache {"
                + this.id
                + "}";
    }

}
