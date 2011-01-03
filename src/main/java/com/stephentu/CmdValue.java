package com.stephentu;

public class CmdValue {
  private final String raw;
  public CmdValue(String raw) {
    this.raw = raw;
  }
  
  public Object coerceTo(Class<?> clazz) {
    if (clazz.equals(String.class))
      return raw;
    if (clazz.equals(Integer.TYPE) || clazz.equals(Integer.class))
      return intHelper.canParse() ? intHelper.parse() : null;
    if (clazz.equals(Boolean.TYPE) || clazz.equals(Boolean.class))
      return boolHelper.canParse() ? boolHelper.parse() : null;
    if (clazz.equals(Long.TYPE) || clazz.equals(Long.class))
      return longHelper.canParse() ? longHelper.parse() : null;
    if (clazz.equals(Short.TYPE) || clazz.equals(Short.class))
      return shortHelper.canParse() ? shortHelper.parse() : null;
    if (clazz.equals(Float.TYPE) || clazz.equals(Float.class))
      return floatHelper.canParse() ? floatHelper.parse() : null;
    if (clazz.equals(Double.TYPE) || clazz.equals(Double.class))
      return doubleHelper.canParse() ? doubleHelper.parse() : null;
    if (clazz.equals(Character.TYPE) || clazz.equals(Character.class))
      return charHelper.canParse() ? charHelper.parse() : null;
    if (clazz.equals(Byte.TYPE) || clazz.equals(Byte.class))
      return byteHelper.canParse() ? byteHelper.parse() : null;
    throw new RuntimeException("Unrecognized class: " + clazz);
  }
  
  public String getRaw() { return raw; }
  
  @Override
  public String toString() { return raw; }
  
  class ParseException extends RuntimeException {
    private static final long serialVersionUID = 3088716858924558883L;
    public ParseException(String msg) { super(msg); }
    public ParseException(Throwable cause) { super(cause); }
  }
  
  abstract class ParseHelper<T> {
    private T parsed;
    protected abstract T doParse();
    private T safeDoParse() {
      T t = doParse();
      if (t == null) throw new RuntimeException("doParse cannot return null");
      return t;
    }
    T parse() {
      if (parsed != null) return parsed;
      parsed = safeDoParse();
      return parsed;
    }
    boolean canParse() {
      try {
        parsed = safeDoParse();
        return true;
      } catch (ParseException e) { return false; }
    }
  }
  
  class IntParseHelper extends ParseHelper<Integer> {
    @Override
    protected Integer doParse() {
      try {
        return Integer.parseInt(raw);
      } catch (NumberFormatException e) { throw new ParseException(e); }
    }
  }
  
  class LongParseHelper extends ParseHelper<Long> {
    @Override
    protected Long doParse() {
      try {
        return Long.parseLong(raw);
      } catch (NumberFormatException e) { throw new ParseException(e); }
    }
  }
  
  class ShortParseHelper extends ParseHelper<Short> {
    @Override
    protected Short doParse() {
      try {
        return Short.parseShort(raw);
      } catch (NumberFormatException e) { throw new ParseException(e); }
    }
  }
  
  class ByteParseHelper extends ParseHelper<Byte> {
    @Override
    protected Byte doParse() {
      try {
        return Byte.parseByte(raw);
      } catch (NumberFormatException e) { throw new ParseException(e); }
    }
  }
  
  class CharParseHelper extends ParseHelper<Character> {
    @Override
    protected Character doParse() {
      if (raw.length() == 1)
        return raw.charAt(0);
      throw new ParseException("Invalid char: " + raw);
    }
  }
  
  class DoubleParseHelper extends ParseHelper<Double> {
    @Override
    protected Double doParse() {
      try {
        return Double.parseDouble(raw);
      } catch (NumberFormatException e) { throw new ParseException(e); }
    }
  }
  
  class FloatParseHelper extends ParseHelper<Float> {
    @Override
    protected Float doParse() {
      try {
        return Float.parseFloat(raw);
      } catch (NumberFormatException e) { throw new ParseException(e); }
    }
  }
  
  
  class BoolParseHelper extends ParseHelper<Boolean> {
    @Override
    protected Boolean doParse() {
      if (raw.equalsIgnoreCase("true"))
        return Boolean.TRUE;
      if (raw.equalsIgnoreCase("false"))
        return Boolean.FALSE;
      throw new ParseException("Invalid boolean: " + raw);
    }
  }
  
  private final ParseHelper<Integer> intHelper = new IntParseHelper();
  private final ParseHelper<Boolean> boolHelper = new BoolParseHelper();
  private final ParseHelper<Long> longHelper = new LongParseHelper();
  private final ParseHelper<Short> shortHelper = new ShortParseHelper();
  private final ParseHelper<Byte> byteHelper = new ByteParseHelper();
  private final ParseHelper<Float> floatHelper = new FloatParseHelper();
  private final ParseHelper<Double> doubleHelper = new DoubleParseHelper();
  private final ParseHelper<Character> charHelper = new CharParseHelper();
}
