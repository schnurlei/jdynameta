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
package de.jdynameta.dbaccess.sql;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public class VisitorTest
{

    public void accept(Visitor aVisitor)
    {

        System.out.println("visited");
    }

    public void accept(VisitorA aVisitor)
    {

        System.out.println("visited by a");
    }

    public void accept(VisitorB aVisitor)
    {

        System.out.println("visited by b");
    }

    public static void main(String[] args)
    {

        VisitorTest test = new VisitorTest();

        Visitor aVisitor = new Visitor();

        test.accept(aVisitor);
    }

    public static class Visitor
    {

        public void visitElement()
        {

            System.out.println("Visitor");
        }
    }

    public static class VisitorA extends Visitor
    {

        @Override
        public void visitElement()
        {

            System.out.println("VisitorA");
        }
    }

    public static class VisitorB extends Visitor
    {

        @Override
        public void visitElement()
        {

            System.out.println("VisitorB");
        }
    }
}
