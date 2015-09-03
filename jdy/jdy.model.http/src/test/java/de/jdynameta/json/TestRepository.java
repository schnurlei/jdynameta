package de.jdynameta.json;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;

class TestRepository extends JdyRepositoryModel
{
	enum Type {
		MASTER, DependentObj, NotDependentObj, AssocDetail
	}

	enum Field {
		MasterKey, MasterValue, DeptKey, DeptValue, NoDeptKey, NoDeptValue, NoDeptRef, DeptRef, AssocDetailKey, AssocDetailValue, AssocRef
	}

	public TestRepository() {
		super("TestRepository");
		JdyClassInfoModel dependent = addClassInfo(Type.DependentObj.name());
		dependent.addTextAttr(Field.DeptKey.name(), 100).setIsKey(true);
		dependent.addTextAttr(Field.DeptValue.name(), 100);
		JdyClassInfoModel notDependent = addClassInfo(Type.NotDependentObj.name());
		notDependent.addTextAttr(Field.NoDeptKey.name(), 100).setIsKey(true);
		notDependent.addTextAttr(Field.NoDeptValue.name(), 100);
		JdyClassInfoModel assocDetail = addClassInfo(Type.AssocDetail.name());
		assocDetail.addTextAttr(Field.AssocDetailKey.name(), 100).setIsKey(true);
		assocDetail.addTextAttr(Field.AssocDetailValue.name(), 100);
		
		
		JdyClassInfoModel master = addClassInfo(Type.MASTER.name());
		master.addTextAttr(Field.MasterKey.name(), 100).setIsKey(true);
		master.addTextAttr(Field.MasterValue.name(), 100);
		master.addReference(Field.NoDeptRef.name(), notDependent).setDependent(false);
		master.addReference(Field.DeptRef.name(), dependent).setDependent(true);

		addAssociation(Field.AssocRef.name(), master, assocDetail, false, true);
		
	}

	public ClassInfo getType(TestRepository.Type aType) {
		
		ClassInfo result = null;
		for( ClassInfo curInfo : this.getAllClassInfosIter())
		{
			if(curInfo.getInternalName().equals(aType.name())) {
				result = curInfo;
				break;
			}
		}
		
		return result;
	}
	
}