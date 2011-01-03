package com.stephentu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdOption {

  private final String name;
  private final List<CmdValue> values;
  
  @SuppressWarnings("unchecked")
  public CmdOption(String name) {
    this(name, Collections.EMPTY_LIST);
  }
  
  public CmdOption(String name, List<String> values) {
    this.name = name;
    this.values = new ArrayList<CmdValue>();
    for (String s : values)
      this.values.add(new CmdValue(s));
  }
 
  public List<CmdValue> getOptions() {
    return values;
  }
  
  public boolean hasNoValues() {
    return values.isEmpty();
  }
  
  public boolean isArray() {
    return values.size() > 1;
  }
  
  public boolean isValue() {
    return values.size() == 1;
  }
  
  @Override
  public String toString() {
    return name + ": " + values;
  }
  
  private static boolean isOptionStr(String s) {
    return s.startsWith("--") && s.length() > 2;
  }
  
  public static Map<String, CmdOption> fromCmdArgs(String[] args) {
    Map<String, CmdOption> opts = new HashMap<String,  CmdOption>();
    for (int i = 0; i < args.length; ) {
      if (isOptionStr(args[i])) {
        String dblDashStripped = args[i].substring(2);
        int pt = dblDashStripped.indexOf('=');
        if (pt == -1) {
          int endPos = i + 1;
          while (endPos < args.length && !isOptionStr(args[endPos])) endPos++;
          List<String> values = new ArrayList<String>();
          for (int j = i + 1; j < endPos; j++) values.add(args[j]);
          opts.put(dblDashStripped, new CmdOption(dblDashStripped, values));
          i = endPos;
        } else {
          String name = dblDashStripped.substring(0, pt);
          String value = dblDashStripped.substring(pt + 1);
          opts.put(name, new CmdOption(name, Collections.singletonList(value)));
          i++;
        }
      } else i++;
    }
    return opts;
  }
  
}
