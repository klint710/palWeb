//package com.example.palWorldManager.controller;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.swing.text.AbstractDocument.BranchElement;
//
//import com.google.gson.Gson;
//
//public class test {
//
//	public static void main(String[] args) {
//    	//路徑
////    	String directoryPath = "/home/qscft821102/palBackup";
//    	String directoryPath = "C:\\Users\\klint\\OneDrive\\Desktop\\pal server";
//    	String jsonStr = "";
//    	// 创建一个 File 对象，表示目录
//        File directory = new File(directoryPath);
//        
//        // 获取目录中的所有文件和目录名
//        String[] files = directory.list();
//        List<Map<String,String>> options = new ArrayList<Map<String,String>>();
//        if (files != null) {
//            for (String fileName : files) {
//            	Map<String,String> map = new HashMap<String,String >();
//            	map.put("key",fileName.substring(0,fileName.length()-4));
//            	map.put("value",fileName);
//            	options.add(map);
//            }
//        } else {
//            System.out.println("目录不存在或者不是一个目录");
//        }
//        Gson gson = new Gson();
//		jsonStr = gson.toJson(options);
//		System.out.println(options);
//	}
//
//}
