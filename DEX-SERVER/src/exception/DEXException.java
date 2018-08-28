package exception;

import configuration.Logs;

public class DEXException extends Exception {
    public DEXException(String message){

        super(message);Logs.log(this.getClass(),message);
    }
}
