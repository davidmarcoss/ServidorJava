/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.infomila.billar.ipersistence;

/**
 *
 * @author David
 */
public class BillarException extends Exception
{
    public BillarException(String message)
    {
        super(message);
    }

    public BillarException(Throwable cause)
    {
        super(cause);
    }

    public BillarException(String message, Throwable ex)
    {
        super(message, ex);
    }
}
