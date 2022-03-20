package mosaic.scheduler.test;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class TestGroovy {
	public static void main(String args[]) throws CompilationFailedException, IOException {
	
		Binding binding = new Binding();
		binding.setVariable("start", 2);
		binding.setVariable("end", 4);
		GroovyShell shell = new GroovyShell(binding);
		Object value = shell.evaluate("for (x=0; x<5; x++){if (x>start && x<end) println(x) else println(-1);}");

		Object test = shell.evaluate("if (3<1) return false else return true");
		
		Object testFile =  shell.evaluate(new File("test.groovy"));
		
		//NOTE: don't forget to add -ea as VM argument to the application in order to enable assertions
		try {
			assert (Boolean)test == true;
		} catch(AssertionError ae) {
			ae.printStackTrace();
		}
	}
}
