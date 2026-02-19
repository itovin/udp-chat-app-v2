/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatappv2.Exceptions;

/**
 *
 * @author itovin
 */
public class InvalidCommandException extends Exception{
    public InvalidCommandException(){
        super("Invalid command!!");
    }
    
    public InvalidCommandException(String msg){
        super(msg);
    }
}
