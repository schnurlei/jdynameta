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
package de.jdynameta.metamodel.generation;

import java.io.IOException;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.generation.PropertyFileGenerator;
import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

public class MetainfoPropertyFileGenerator extends PropertyFileGenerator
{

    public MetainfoPropertyFileGenerator(PropertyNameCreator aPropertyNameGenerator)
    {
        super(aPropertyNameGenerator);
    }

    public static void main(String[] args)
    {
        try
        {
            MetainfoPropertyFileGenerator generator = new MetainfoPropertyFileGenerator(new DefaultPropertyNameCreator());

            String path = "resources/"; //+MetaModelRepository.class.getPackage().getName().replace('.', '/');

            generator.generatePropertyFileForRepository(new MetaModelRepository(), "metamodel", path);
        } catch (IOException | InvalidClassInfoException excp)
        {
            excp.printStackTrace();
        }
    }
}
