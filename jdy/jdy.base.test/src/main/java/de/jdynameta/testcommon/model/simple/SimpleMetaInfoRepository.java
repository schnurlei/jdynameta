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
package de.jdynameta.testcommon.model.simple;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;

/**
 * @author rsc
 */
public class SimpleMetaInfoRepository extends JdyRepositoryModel
{
    private static final SimpleMetaInfoRepository singleton = new SimpleMetaInfoRepository();

    private final JdyClassInfoModel companyInfo;
    private final JdyClassInfoModel contactInfo;
    private final JdyClassInfoModel noteInfo;
    private final JdyClassInfoModel employeeInfo;
    private final JdyClassInfoModel chiefEmployeeInfo;
    /**
     * Class with Reference to another class in primary Key
     */
    private final JdyClassInfoModel referenceInPrimaryKeyInfo;

    public SimpleMetaInfoRepository()
    {
        super("SimpleRepository");
        this.addListener(new DefaultClassRepositoryValidator());

        this.companyInfo = this.addClassInfo("Company").setExternalName("CompanyEx");
        this.companyInfo.addLongAttr("CompanyId", 0, Integer.MAX_VALUE).setIsKey(true);
        this.companyInfo.addTextAttr("CompanyName", 1000).setIsKey(true);
        this.companyInfo.addTextAttr("Zip", 10);
        this.companyInfo.addTextAttr("City", 60);
        this.companyInfo.addTextAttr("Street", 30);

        this.contactInfo = this.addClassInfo("Contact").setExternalName("ContactEx");
        this.contactInfo.addLongAttr("ContactId", 0, Integer.MAX_VALUE).setIsKey(true);
        this.contactInfo.addTextAttr("ContactName", 1000);
        this.contactInfo.addTextAttr("Phone", 1000);
        this.contactInfo.addReference("Company", this.companyInfo);

        this.noteInfo = this.addClassInfo("Note").setExternalName("NoteEx");
        this.noteInfo.addReference("Company", this.companyInfo).setIsKey(true);
        this.noteInfo.addReference("Contact", this.contactInfo).setIsKey(true);
        this.noteInfo.addFloatAttr("NoteValue");

        this.employeeInfo = this.addClassInfo("Employee").setExternalName("EmployeeEx");
        this.employeeInfo.addLongAttr("EmployeeId", 0, Integer.MAX_VALUE).setIsKey(true);
        this.employeeInfo.addTextAttr("EmployeeName", 1000).setNotNull(true);
        this.employeeInfo.addReference("Company", this.companyInfo).setNotNull(true);

        this.chiefEmployeeInfo = this.addClassInfo("ChiefEmployee", this.employeeInfo).setExternalName("ChiefEmployeeEx");
        this.chiefEmployeeInfo.addLongAttr("Bonus", 0, Integer.MAX_VALUE);
        this.chiefEmployeeInfo.addTimestampAttr("BonusDate", true, false);

        this.referenceInPrimaryKeyInfo = this.addClassInfo("ReferenceInPrimaryKey").setExternalName("ReferenceInPrimaryKeyEx");
        this.referenceInPrimaryKeyInfo.addReference("Company", this.companyInfo).setIsKey(true);
        this.referenceInPrimaryKeyInfo.addTextAttr("Value1", 1000);

    }

    public ClassInfo getCompanyClassInfo()
    {
        return this.companyInfo;
    }

    public ClassInfo getContactClassInfo()
    {
        return this.contactInfo;
    }

    public ClassInfo getNoteClassInfo()
    {
        return this.noteInfo;
    }

    public ClassInfo getEmployeeClassInfo()
    {

        return this.employeeInfo;
    }

    public ClassInfo getChiefEmployeeClassInfo()
    {
        return this.chiefEmployeeInfo;
    }

    public ClassInfo getReferenceInPrimaryKeyClassInfo()
    {
        return this.referenceInPrimaryKeyInfo;
    }

    public static SimpleMetaInfoRepository getSingleton()
    {
        return singleton;
    }

}
