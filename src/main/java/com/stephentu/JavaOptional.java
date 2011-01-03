package com.stephentu;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.stephentu.util.Tuple2;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class JavaOptional {

  private static Object coerceTo(Class<?> clazz, CmdOption opt) {
    if (opt.isArray() && clazz.isArray()) // cannot support arrays yet
      throw new UnsupportedOperationException("Array parameters not implemented yet");
    if (opt.isArray()) // input is array but clazz is not
      return null;
    if (opt.hasNoValues()) // flags require explicit true/false for now
      return null;
    assert opt.isValue();
    return opt.getOptions().get(0).coerceTo(clazz);
  }
  
  private static String prettyStringOf(Tuple2<Method, String[]> tup) {
    String[] prettyNames = new String[tup._2.length];
    Class<?>[] types = tup._1.getParameterTypes();
    for (int i = 0; i < prettyNames.length; i++) 
      prettyNames[i] = tup._2[i] + ": " + types[i].toString();
    return Arrays.toString(prettyNames);
  }
  
  public static void runMain(Class<?> clazz, String[] args) {
    Map<String, CmdOption> parsed = CmdOption.fromCmdArgs(args);
    //System.out.println(parsed);
    Paranamer p = new BytecodeReadingParanamer();
    
    List<Tuple2<Method, Object[]>> candidates = new ArrayList<Tuple2<Method, Object[]>>();
    List<Tuple2<Method, String[]>> candOptMains = new ArrayList<Tuple2<Method, String[]>>();
    List<Tuple2<Method, String[]>> optMains = new ArrayList<Tuple2<Method, String[]>>();
    for (Method m : clazz.getMethods()) {
      if (m.getName().equals("optMain") && 
          (m.getModifiers() & Modifier.PUBLIC) != 0 &&
          (m.getModifiers() & Modifier.STATIC) != 0 &&
          (m.getReturnType().equals(Void.TYPE) || m.getReturnType().equals(Void.class))) {
        
        String[] names = p.lookupParameterNames(m);
        Class<?>[] types = m.getParameterTypes();
        assert names.length == types.length : "names and type array lengths do not match";
        optMains.add(new Tuple2<Method, String[]>(m, names));
        
        boolean qualifies = false;
        Object[] optMainArgs = new Object[names.length];
        for (int i = 0; i < names.length; i++) {
          CmdOption cmdOpt = parsed.get(names[i]);
          if (cmdOpt == null)
            break;
          Object o = coerceTo(types[i], cmdOpt);
          if (o == null)
            break;
          optMainArgs[i] = o;
          if (i == names.length - 1)
            qualifies = true;
        }
        
        if (qualifies) {
          candidates.add(new Tuple2<Method, Object[]>(m, optMainArgs));
          candOptMains.add(new Tuple2<Method, String[]>(m, names));
        }
      }
    }
    
    if (candidates.isEmpty()) { 
      System.err.println("No qualifying optMain method for given command line arguements:");
      System.err.println("  " + Arrays.toString(args));
      System.err.println("Candidates are:");
      for (Tuple2<Method, String[]> optMain : optMains) 
        System.err.println("  " + prettyStringOf(optMain));
      System.exit(1);
    } else if (candidates.size() > 1) {
      System.err.println("Ambiguous optMain methods for given command line arguments:");
      System.err.println("  " + Arrays.toString(args));
      System.err.println("Ambiguous candidates are:");
      for (Tuple2<Method, String[]> cand : candOptMains) 
        System.err.println("  " + prettyStringOf(cand));
      System.exit(2);
    } else {
      Tuple2<Method, Object[]> c = candidates.get(0);
      try {
        c._1.invoke(null, c._2);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }
  }

}