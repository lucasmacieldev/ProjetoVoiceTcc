package com.example.lucasmaciel.testevoice.genericalarmclock.model;

/**
 * Created by cezar on 12/9/16.
 */

public class WeekInformation
{
   private static String[] _vetDiasSemana = {"Domingo","Segunda","Terca","Quarta","Quinta","Sexta","Sábado"};
   private static String[] _vetReduzido = {"Dom","Seg","Ter","Qua","Quin","Sex","Sab"};
   public static String[] get_vetDiasSemana() {return _vetDiasSemana;}
   public static String[] get_vetReduzido() {return _vetReduzido;}
}
