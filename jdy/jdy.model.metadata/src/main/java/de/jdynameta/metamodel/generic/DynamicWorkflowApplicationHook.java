package de.jdynameta.metamodel.generic;

import de.jdynameta.application.WorkflowPersistenceHook;
import de.jdynameta.application.ApplicationManagerImpl.WorkflowApplicationHook;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.metamodel.application.AppClassInfo;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.generic.ScritpingHook.ScriptSource;
import de.jdynameta.persistence.state.ApplicationObj;

public class DynamicWorkflowApplicationHook extends WorkflowApplicationHook<ApplicationObj>
{
    private final AppRepository appRepository;

    public DynamicWorkflowApplicationHook(AppRepository anAppRepository)
    {
        this.appRepository = anAppRepository;
    }

    @Override
    public WorkflowPersistenceHook<ApplicationObj> getWorkflowPersistenceHookFor(ClassInfo aClassInfo)
    {
        WorkflowPersistenceHook<ApplicationObj> hook = super.getWorkflowPersistenceHookFor(aClassInfo);

        if (hook == null)
        {

            AppClassInfo info = getAppClassforClassInfo(aClassInfo);
            if (info != null)
            {
                hook = createScritpingHook(info);
                setPersistenceHookForClass(aClassInfo, hook);
            }
        }

        return hook;
    }

    private AppClassInfo getAppClassforClassInfo(ClassInfo aClassInfo)
    {
        AppClassInfo result = null;

        for (AppClassInfo curAppClass : appRepository.getClassesColl())
        {
            if (curAppClass.getName().equals(aClassInfo.getInternalName()))
            {
                result = curAppClass;
            }
        }

        return result;

    }

    /**
     * @param aScriptSource
     * @return
     */
    protected ScritpingHook createScritpingHook(ScriptSource aScriptSource)
    {
        return new ScritpingHook(aScriptSource);
    }

}
