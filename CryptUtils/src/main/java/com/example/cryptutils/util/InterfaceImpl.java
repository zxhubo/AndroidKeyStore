package com.example.cryptutils.util;

import com.example.cryptutils.myinterface.InterfaceA;
import com.example.cryptutils.myinterface.InterfaceB;

import java.lang.reflect.Type;

public class InterfaceImpl implements InterfaceA,InterfaceB {
    @Override
    public String showA() {
        return "impl InterfaceA";
    }

    @Override
    public String showB() {
        return "impl InterfaceB";
    }

//    public static void main(String args[]){
//        for (Class c:InterfaceImpl.class.getInterfaces()) {
//
//            System.out.println(c.getName());
//
//        }
//        for(Type type:InterfaceImpl.class.getGenericInterfaces()){
//            System.out.println(type.getTypeName());
//            System.out.println();
//        }
//    }
}
