public class Tester implements TestInterface 
{
    public void printIt(de.jdynameta.base.metainfo.ClassInfo classInfo) 
    {
        println "this is in the test class " + classInfo.getInternalName();
    }
}
