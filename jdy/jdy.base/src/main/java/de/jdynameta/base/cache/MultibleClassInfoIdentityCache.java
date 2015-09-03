/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.base.cache;

import java.io.Serializable;
import java.util.Hashtable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages caches depending on the classInfo Every classinfo has its one
 * SingelClassInfoIdentityCache
 *
 * @author rsc
 */
@SuppressWarnings("serial")
public class MultibleClassInfoIdentityCache<TCachedObj> implements Serializable {

    private final Map<ClassInfo, SingelClassInfoIdentityCache<TCachedObj>> classInfo2CacheMap;

    /**
     *
     */
    public MultibleClassInfoIdentityCache() {
        super();
        this.classInfo2CacheMap = new ConcurrentHashMap<ClassInfo, SingelClassInfoIdentityCache<TCachedObj>>();
    }

    /**
     *
     * @author	Rainer Schneider
     * @return boolean
     */
    public TCachedObj getObject(TypedValueObject aValueModel) {
        return this.getCacheForClassInfo(aValueModel.getClassInfo()).getObject(aValueModel);
    }

    public void insertObject(TypedValueObject aValueModel, TCachedObj aNewObj) {
        SingelClassInfoIdentityCache<TCachedObj> curCache = getCacheForClassInfo(aValueModel.getClassInfo());
        curCache.insertObjectForValueModel(aValueModel, aNewObj);
    }

    public SingelClassInfoIdentityCache<TCachedObj> getCacheForClassInfo(ClassInfo aInfo) {
        SingelClassInfoIdentityCache<TCachedObj> resultCache = null;

        if (this.classInfo2CacheMap.containsKey(aInfo)) {
            resultCache = this.classInfo2CacheMap.get(aInfo);
        } else {
            resultCache = createCacheForClassInfo(aInfo);
            this.classInfo2CacheMap.put(aInfo, resultCache);
        }

        return resultCache;
    }

    protected SingelClassInfoIdentityCache<TCachedObj> createCacheForClassInfo(ClassInfo aInfo) {
        return new SingelClassInfoIdentityCache<>(aInfo, 100);
    }

    public void insertObject(ValueObject aValueModel, TCachedObj aNewObj, ClassInfo aClassInfo) {
        SingelClassInfoIdentityCache<TCachedObj> curCache = getCacheForClassInfo(aClassInfo);
        curCache.insertObjectForValueModel(aValueModel, aNewObj);
    }

}
