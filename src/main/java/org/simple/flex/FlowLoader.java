package org.simple.flex;

import org.apache.camel.builder.RouteBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

//import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlowLoader {
    static final String NIX_HOME = "~";

    private String fileFullPath;
    private String fileNameExt;
    private String fileName;
    private String packageName;
    private boolean isFileExists;
    private Path path;
    private Path compiled;

    public FlowLoader( String fileFullPath ) {
        this.isFileExists = false;
        if( fileFullPath.startsWith( this.NIX_HOME ))
            fileFullPath = fileFullPath.replace(this.NIX_HOME, System.getProperty("user.home"));

        this.path = Paths.get( fileFullPath );
        this.isFileExists = this.path.toFile().exists();
        if( this.isFileExists ){
            this.fileFullPath = fileFullPath;
            this.fileNameExt = this.path.getFileName().toString();
            this.fileName = this.fileNameExt.split("\\.")[0];

            this.packageName = this.getPackage();
        }
    }

    private String getPackage(){
        String result = "";

        Pattern pattern = Pattern.compile("package .*+$");
        Matcher matcher = pattern.matcher( "");

        try {
            Matcher mt = Files.lines(this.path)
                    .map(matcher::reset)
                    .filter(Matcher::matches)
                    .findFirst()
                    .orElse(null);

            if( (mt != null) && (mt.matches())) {
                result = mt.group();

                if (result.length() > 0) {
                    result = (result.replace(";", " "));
                    result = (result.replace("package", " ").trim());
                }
            }

        }
        catch(IOException e){
            result = "";
        }
        return result;
    }

    public String javaClassPath(){
        if( this.packageName.equals("") ) return this.fileName;
        else return this.packageName + "." + this.fileName;
    }

    public boolean isFileExists(){
        return this.isFileExists;
    }

    private boolean isCompiledExist(){
        this.compiled = Paths.get( this.path.toFile().getParentFile().toString() + File.separator + this.fileName + ".class" );
        return compiled.toFile().exists();
    }

    private boolean isCompile(){
        if( this.isCompiledExist() ) {
            if (this.compiled.toFile().lastModified() > this.path.toFile().lastModified())
                return false;
            else
                return true;
        }
        return false;
    }

    public RouteBuilder run() throws  Exception{
        File src = this.path.toFile();
        RouteBuilder rb;
        System.out.println("-----------------------------------------");
        System.out.println("Compile: " + this.fileFullPath );
        System.out.println("Package: " + this.getPackage() );
        System.out.println("Class: " + this.javaClassPath() );

        // compile the source file
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null)) {

            if( this.isCompile() ) {
                // Compile java
                try {
                    JavaCompiler.CompilationTask task = compiler.getTask(
                            null, fm, null,
                            null, null,
                            fm.getJavaFileObjects(src));
                    task.call();
                } catch (Exception ex) {
                    System.out.println("LEVEL: Compile");
                    System.out.println("ERROR: ");
                    ex.printStackTrace();
                    throw new java.lang.Exception(ex.getMessage());
                }
            }
            else{
                System.out.println("Compiled file is exist");
            }
            try {
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{src.getParentFile().toURI().toURL()});
                Class cls = classLoader.loadClass(this.javaClassPath());
                rb = (RouteBuilder) cls.newInstance();
            }
            catch(Exception ex){
                System.out.println("LEVEL: Class loading" );
                System.out.println("ERROR: ");
                ex.printStackTrace();
                throw new java.lang.Exception( ex.getMessage());
            }
            System.out.println("Class loaded." );
            System.out.println("-----------------------------------------");

            return rb;
        }
    }
}
