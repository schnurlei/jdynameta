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

public class JavaFileParser
{
//    public static void main(String[] args) {
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
//        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
//        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects("path/to/Source.java");
//        CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
//
//        // Here we switch to Sun-specific APIs
//        JavacTask javacTask = (JavacTask) task;
//        SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
//        Iterable<? extends CompilationUnitTree> parseResult = null;
//        try {
//                parseResult = javacTask.parse();
//        } catch (IOException e) {
//
//                // Parsing failed
//                e.printStackTrace();
//                System.exit(0);
//        }
//        for (CompilationUnitTree compilationUnitTree : parseResult) {
//                compilationUnitTree.accept(new MethodLineLogger(compilationUnitTree, sourcePositions), null);
//        }
//    }
//	
//	
//	
//	
//	  public static void main1(String args[]) throws IOException
//	  {
//	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
//	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
//	    Iterable<? extends JavaFileObject> compilationUnits = fileManager
//	        .getJavaFileObjectsFromStrings(Arrays.asList("Foo.java"));
//	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null,
//	        null, compilationUnits);
//	    boolean success = task.call();
//	    fileManager.close();
//	    System.out.println("Success: " + success);
//	  }	
//
//	public void parser()
//	{
//		//Get an instance of java compiler
//		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//
//		//Get a new instance of the standard file manager implementation
//		StandardJavaFileManager fileManager = compiler.
//		getStandardFileManager(null, null, null);
//
//		// Get the list of java file objects, in this case we have only
//		// one file, TestClass.java
//		Iterable compilationUnits1 =
//		fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File("Foo.java")));
//		
//	}
//	
//	   private static class MethodLineLogger extends TreeScanner<Void, Void> {
//	        private final CompilationUnitTree compilationUnitTree;
//	        private final SourcePositions sourcePositions;
//	        private final LineMap lineMap;
//
//	        private MethodLineLogger(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {
//	                this.compilationUnitTree = compilationUnitTree;
//	                this.sourcePositions = sourcePositions;
//	                this.lineMap = compilationUnitTree.getLineMap();
//	        }
//
//	        @Override
//	        public Void visitMethod(MethodTree arg0, Void arg1) {
//	                long startPosition = sourcePositions.getStartPosition(compilationUnitTree, arg0);
//	                long startLine = lineMap.getLineNumber(startPosition);
//	                long endPosition = sourcePositions.getEndPosition(compilationUnitTree, arg0);
//	                long endLine = lineMap.getLineNumber(endPosition);
//
//	                // Voila!
//	                System.out.println("Found method " + arg0.getName() + " from line " + startLine + " to line "  + endLine + ".");
//
//	                return super.visitMethod(arg0, arg1);
//	        }
//	    }
//	
}
