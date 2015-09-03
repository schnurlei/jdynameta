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
package de.jdynameta.base.metainfo.primitive;

import java.math.BigDecimal;
import java.util.Date;

import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public interface PrimitiveTypeGetVisitor
{
    public Long handleValue(LongType aType) throws JdyPersistentException;

    public Boolean handleValue(BooleanType aType) throws JdyPersistentException;

    public Double handleValue(FloatType aType) throws JdyPersistentException;

    public Date handleValue(TimeStampType aType) throws JdyPersistentException;

    public BigDecimal handleValue(CurrencyType aType) throws JdyPersistentException;

    public String handleValue(TextType aType) throws JdyPersistentException;

    public String handleValue(VarCharType aType) throws JdyPersistentException;

    public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException;
}
