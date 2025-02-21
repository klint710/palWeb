package com.example.palWorldManager.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountException;
import javax.sound.sampled.Line;

import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.hibernate.engine.jdbc.ReaderInputStream;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.objenesis.strategy.PlatformDescription;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.palWorldManager.model.QryLogData;

import com.google.gson.Gson;

@RequestMapping("/manager")
@RestController
public class PalWorldManager {
//	public static void main(String[] args) {
//        try {
//            // 创建一个ProcessBuilder对象
//            ProcessBuilder builder = new ProcessBuilder();
//            // 设置命令和参数
//            builder.command("./123.sh");
//            // 启动进程
//            Process process = builder.start();
//            // 等待进程执行完毕并获取结果码
//            int exitCode = process.waitFor();
//            System.out.println("Exited with error code: " + exitCode);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

	private String $HOME = System.getProperty("user.home");
	

	public String get$HOME() {
		return $HOME;
	}

	public void set$HOME(String $home) {
		$HOME = $home;
	}

	@RequestMapping("/login")
	public String login(@RequestParam("password") String password) {
		if (password != null && "1234".equals(password)) {
			return "1";
		}
		return "0";
	}

	@RequestMapping("/run")
	public String run() {
		try {
			// 创建一个ProcessBuilder对象
			ProcessBuilder builder = new ProcessBuilder();
			// 设置命令和参数
			builder.command($HOME + "/palData/restart.sh");
			// 启动进程
			Process process = builder.start();
			// 等待进程执行完毕并获取结果码
			int exitCode = process.waitFor();
			System.out.println("run exited with error code: " + exitCode);
			if (exitCode != 0) {
				return "0";
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	@RequestMapping("/shutdown")
	public String shutdown() {
		try {
			// 创建一个ProcessBuilder对象
			ProcessBuilder builder = new ProcessBuilder();
			// 设置命令和参数
			builder.command($HOME + "/palData/closeDay.sh");
			// 启动进程
			Process process = builder.start();
			// 等待进程执行完毕并获取结果码
			int exitCode = process.waitFor();
			System.out.println("shutdown exited with error code: " + exitCode);
			if (exitCode != 0) {
				return "0";
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	@RequestMapping("/updateVersion")
	public String updateVersion() {
		try {
			// 停止server
			// 创建一个ProcessBuilder对象
			ProcessBuilder builder = new ProcessBuilder();
			// 设置命令和参数
			builder.command($HOME + "/palData/closeDay.sh");
			// 启动进程
			Process process = builder.start();
			// 等待进程执行完毕并获取结果码
			int exitCode = process.waitFor();
			System.out.println("updateVersion exited with error code1: " + exitCode);
			if (exitCode != 0) {
				return "0";
			}

			// 更新版本
			// 创建一个ProcessBuilder对象
			ProcessBuilder builder2 = new ProcessBuilder();
			// 设置命令和参数
			builder2.command($HOME + "/palData/updateVersion.sh");
			// 启动进程
			Process process2 = builder2.start();
			// 等待进程执行完毕并获取结果码
			int exitCode2 = process2.waitFor();
			System.out.println("updateVersion exited with error code2: " + exitCode2);
			if (exitCode2 != 0) {
				return "0";
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	@RequestMapping("/getServerIsLive")
	public String getServerIsLive() {
		try {

			ProcessBuilder builder = new ProcessBuilder();
			// 设置命令和参数
			builder.command($HOME + "/palData/getServerIsLive.sh");
			// 启动进程
			Process process = builder.start();

			// 等待进程执行完成
			int exitCode = process.waitFor();
			System.out.println("getServerIsLive exited with error code: " + exitCode);
			if (exitCode != 0) {
				return "0";
			}

			// 从进程获取标准输出流
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			// 使用 BufferedReader 读取输出流
			BufferedReader reader = new BufferedReader(isr);

			// 读取输出内容
			String line;
			while ((line = reader.readLine()) != null) {
				if ("Shutdown handler: cleanup.".equals(line) || "Exiting".equals(line)) {
					reader.close();
					return "2"; // not live
				}
			}

			// 关闭流
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1"; // live
	}

	@RequestMapping("/getPlayers")
	public String getPlayers() {
//    	String filePath = "C:\\Users\\klint\\OneDrive\\Desktop\\pal server\\test.txt";
		String filePath = $HOME + "/palData/log/logfile.log";
		String jsonStr = "";
		try {
			// 创建 FileInputStream 对象
			InputStream is = new FileInputStream(filePath);

			// 使用 BufferedReader 包装 InputStreamReader，以便按行读取文件内容
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);

			// 读取整个文件内容
			StringBuilder sb = new StringBuilder();
			String line;
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			List<String> playList = new ArrayList<String>();
			List<String> livestatus = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				if (line.contains("User id")) {
					System.out.println(line);
					String[] st = line.split(" ");
					if ("joined".equals(st[4])) {
						playList.add(st[3]);
					} else if ("left".equals(st[4])) {
						playList.remove(st[3]);
					}
				}
				if (line.contains("Exit") || line.contains("exit")) {
					livestatus.add("2");// 關閉
				}
			}
			map.put("players", playList);
			map.put("livestatus", livestatus);
			Gson gson = new Gson();
			jsonStr = gson.toJson(map);
			// 关闭流
			reader.close();
			isr.close();
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
			return "0";
		}
		return jsonStr;
	}

	@RequestMapping("/getFileList")
	public String getFileList() {
		// 路徑
    	String directoryPath = $HOME+"/palData/palBackup";
//		String directoryPath = "C:\\Users\\klint\\OneDrive\\Desktop\\pal server";
		String jsonStr = "";
		// 创建一个 File 对象，表示目录
		File directory = new File(directoryPath);

		// 获取目录中的所有文件和目录名
		String[] files = directory.list();
		List<Map<String, String>> options = new ArrayList<Map<String, String>>();
		if (files != null) {
			for (String fileName : files) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("key", fileName.replace(".log", ""));
				map.put("value", fileName.replace(".log", ""));
				options.add(map);
			}
		} else {
			System.out.println("目录不存在或者不是一个目录");
		}
		Gson gson = new Gson();
		jsonStr = gson.toJson(options);
		return jsonStr;
	}

	@RequestMapping("/revert")
	public String revert() {
		String filePath = "C:\\Users\\klint\\OneDrive\\Desktop\\pal server\\test.txt";
//    	String filePath = $HOME+"/palData/log/logfile.log";
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		String jsonStr = "";
		try {
			// 创建 FileInputStream 对象
			is = new FileInputStream(filePath);

			// 使用 BufferedReader 包装 InputStreamReader，以便按行读取文件内容
			isr = new InputStreamReader(is);
			reader = new BufferedReader(isr);

			// 读取整个文件内容
			StringBuilder sb = new StringBuilder();
			String line;
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			List<String> playList = new ArrayList<String>();
			List<String> livestatus = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("target=")) {

				}
			}
			map.put("players", playList);
			map.put("livestatus", livestatus);
			Gson gson = new Gson();
			jsonStr = gson.toJson(map);
			// 关闭流
			reader.close();
			isr.close();
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	@RequestMapping("/downloadLog")
	public ResponseEntity<InputStreamResource> downLoadLog(@RequestBody QryLogData params) {
		try {
			String fileName = params.getFileName();
			File file = new File($HOME + "/palData/log/" + fileName);
			if (!file.exists()) {
				return ResponseEntity.notFound().build();
			}

			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);

			// 使用 InputStreamResource 包裝 BufferedInputStream
			InputStreamResource resource = new InputStreamResource(bis);

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
					.contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(file.length()).body(resource);

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
	
//	@RequestMapping("/test")
//	public ResponseEntity<InputStreamResource> test(@RequestBody QryLogData params) {
//		try {
//			String fileName = params.getFileName();
//			File file = new File($HOME + "/palData/log/" + fileName);
//			if (!file.exists()) {
//				return ResponseEntity.notFound().build();
//			}
//
//			InputStream is = new FileInputStream(file);
//			BufferedInputStream bis = new BufferedInputStream(is);
//
//			// 使用 InputStreamResource 包裝 BufferedInputStream
//			InputStreamResource resource = new InputStreamResource(bis);
//
//			return ResponseEntity.ok()
//					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
//					.contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(file.length()).body(resource);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			return ResponseEntity.notFound().build();
//		}
//	}
	
}
