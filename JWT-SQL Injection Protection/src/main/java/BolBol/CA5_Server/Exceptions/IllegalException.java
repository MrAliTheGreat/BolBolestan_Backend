package BolBol.CA5_Server.Exceptions;

public class IllegalException extends Exception{
    @Override
    public String getMessage() {
        return "hasIllegalChars called! There were illegal characters in input text";
    }
}