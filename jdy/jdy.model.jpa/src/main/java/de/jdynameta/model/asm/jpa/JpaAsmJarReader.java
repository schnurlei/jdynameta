/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;

import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.model.asm.jpa.info.JpaAsmClassInfo;

/**
 *
 * @author rainer
 */
public class JpaAsmJarReader
{

    public static void main(final String[] args)
            throws IOException, InterruptedException
    {

        if (args.length > 0)
        {
            String jar = args[0];
            List<JpaAsmClassInfo> allJpaInfos = new JpaAsmJarReader().loadJpaInfoFromJar(jar);
            System.out.println("####################");

            JpaAsmRepositoryCreator repoCreator = new JpaAsmRepositoryCreator(null);
            ClassRepository result = repoCreator.createMetaRepository(allJpaInfos, "jdymodel");

            System.out.println("##########" + result.getRepoName());
        }
    }

    public List<JpaAsmClassInfo> loadJpaInfoFromJar(final String jar)
            throws IOException, InterruptedException
    {
        String clazz = System.getProperty("asm.test.class");

        List<JpaAsmClassInfo> allJpaInfos = new ArrayList<>();

        ZipFile zip;
        try
        {
            zip = new ZipFile(jar);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry e = entries.nextElement();
                String fileName = e.getName();
                if (fileName.endsWith(".class"))
                {
                    fileName = fileName.substring(0, fileName.length() - 6).replace('/', '.');
                    if (clazz == null || fileName.contains(clazz))
                    {
                        allJpaInfos.add(handleClassStream(zip, e));
                    }
                }
            }
            zip.close();
        } catch (Exception e)
        {
            System.err.println("Error openning " + jar);
            e.printStackTrace();
        }

        return allJpaInfos;
    }

    private static JpaAsmClassInfo handleClassStream(ZipFile zip, ZipEntry e) throws IOException
    {

        JpaAsmClassReader visitor;
        try (InputStream is = zip.getInputStream(e))
        {
            ClassReader reader = new ClassReader(is);
            visitor = new JpaAsmClassReader();
            reader.accept(visitor, 0);
        }

        return visitor.getJpaInfo();
    }

}
