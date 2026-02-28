/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exceptions;

/**
 *
 * @author itovin
 */
public class UserAlreadyLoggedInException extends Exception {
    public UserAlreadyLoggedInException(String msg){
        super(msg);
    }
}
