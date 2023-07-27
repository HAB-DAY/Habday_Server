package com.habday.server.constants;

public class CmnConst {
    public static String server = "http://13.124.209.40:8080/";
    public static String localhost = "http://localhost:9000/";
    //TODO 스케쥴 시간 등 중요한 정보 넣기
    public static int paymentDelayMin = 1080;//분 단위
    public static int confirmLimitDate = 14; //일 단위
    public static final String scheduleCron = "0 5 0 * * *"; //"0 5 0 * * *"
}
