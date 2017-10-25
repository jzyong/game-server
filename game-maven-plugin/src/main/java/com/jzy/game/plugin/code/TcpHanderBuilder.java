package com.jzy.game.plugin.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.jzy.game.engine.util.FileUtil;

/**
 * TCP消息处理器生成
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月18日 下午7:42:49
 */
@Mojo(name = "tcpHandler", defaultPhase = LifecyclePhase.INITIALIZE)
public class TcpHanderBuilder extends AbstractMojo {
	/** 默认包含的消息文件路径 */
	private static final String[] DEFAULT_INCLUDES = new String[] { "Message.java" };

	/**
	 * 项目根目录
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project.basedir}")
	private File basedir;

	/**
	 * 项目资源目录
	 *
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project.build.sourceDirectory}")
	private File sourceDirectory;

	/**
	 * 项目测试资源目录
	 *
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project.build.testSourceDirectory}")
	private File testSourceDirectory;

	/**
	 * 项目资源
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project.build.resources}")
	private List<Resource> resources;

	/**
	 * 项目测试资源
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project.build.testResources}")
	private List<Resource> testResources;

	/** 消息类路径 */
	@Parameter(defaultValue = "/com/jjy/game/message/hall")
	private String messagePath;

	/** 脚本生成类路径 */
	@Parameter(defaultValue = "\\com\\jjy\\game\\hall\\tcp")
	private String scriptPath;

	/** 消息目录 */
	private File msgBaseDir;
	/** handler目录 */
	protected File handlerBaseDir;

	/** 匹配包路径 */
	private static final String packagePatten = "package ";
	/** 匹配消息类 */
	private static final String messagePatten = "(.*)public static final class (.*)Request extends(.*)";

	public void execute() throws MojoExecutionException, MojoFailureException {
		String basePath = basedir.getPath();
		getLog().info(basePath);
		// protobuf 消息绝对路径
		String msgPath = basePath.substring(0, basePath.lastIndexOf("\\")) + File.separatorChar + "game-message"
				+ File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar + "java" + messagePath;
		String handlerPath = sourceDirectory.getPath() + scriptPath;

		msgBaseDir = new File(msgPath);
		handlerBaseDir = new File(handlerPath);
		getLog().info("protobuf路径:" + msgPath);
		getLog().info("handler路径:" + handlerPath);
		List<File> msgFiles = new ArrayList<File>();
		List<File> handlerFiles = new ArrayList<File>();

		FileUtil.getFiles(msgBaseDir, msgFiles, "Message.java", null);
		FileUtil.getFiles(handlerPath, handlerFiles, "Handler.java", null);
		List<String> handlerNames = handlerFiles.stream().map(file -> file.getName()).collect(Collectors.toList());
		for (File file : msgFiles) {
			try {
				getLog().info(createTcpHandler(file, handlerPath, handlerNames));
			} catch (Exception e) {
				getLog().error(String.format("生成 类的handler失败", file.getName()), e);
			}
		}
	}

	/**
	 * 生成handler
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年10月19日 下午5:06:31
	 * @param file
	 *            protobuf消息文件
	 * @param handlerNames
	 *            已存在的handler
	 * @return
	 */
	private String createTcpHandler(File file, String handlerPath, List<String> handlerNames) throws Exception {

		// 读取protobuf消息类信息
		List<String> classNames = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String packageName = ""; // 包路径
		while (br.ready()) {
			String readLine = br.readLine();
			if (packageName.equals("") && readLine.startsWith(packagePatten)) {
				packageName = readLine.replace(packagePatten, "").replace(";", "");// + ".handler";
			} else if (readLine.matches(messagePatten)) {
				int indexOf0 = readLine.indexOf("class ") + 6; // 类名开始下标
				int indexOf1 = readLine.indexOf(" extends"); // 类名结束下标
				String className = readLine.substring(indexOf0, indexOf1);
				// getLog().info("className:" + className);
				classNames.add(className);
			}
		}
		br.close();
		if (classNames.isEmpty()) {
			return String.format("消息类:%s没有定义任何Handler类", file.getName());
		}
		String handlerPackage = scriptPath + File.separatorChar + getSubPackageDir(file);
		handlerPackage = handlerPackage.replaceAll("\\\\", "."); // handler包路径
		if(handlerPackage.startsWith(".")) {
			handlerPackage=handlerPackage.substring(1);
		}

		StringBuffer info = new StringBuffer(file.getName()+"生成handler:\n");
		// 创建handler类文件
		for (String className : classNames) {
			String handlerClassName = className.replace("Request", "Handler"); // handler 类名
			String fileName = className.replace("Request", "Handler.java"); // handler文件名
			if (handlerNames.contains(fileName)) {
				continue;
			}
			String filePath = handlerPath + File.separatorChar + getSubPackageDir(file) + File.separatorChar + fileName;
			File handlerFile = new File(filePath);
			if (handlerFile.exists()) {
				continue;
			}
			// 组装类文件
			StringBuffer sb = new StringBuffer();
			sb.append("/**工具生成，请遵循规范<br>\n @auther JiangZhiYong\n */\n");
			// 包路径
			sb.append("package ").append(handlerPackage).append(";\n\n");
			// 引入依赖
			sb.append("import com.jzy.game.engine.handler.HandlerEntity;\n");
			sb.append("import com.jzy.game.engine.handler.TcpHandler;\n");
			sb.append("import com.jjy.game.message.Mid.MID;\n");
			sb.append("import org.slf4j.Logger;\n");
			sb.append("import org.slf4j.LoggerFactory;\n");
			sb.append("import ").append(packageName).append(".")
					.append(file.getName().substring(0, file.getName().indexOf("java"))).append(className)
					.append(";\n\n\n");

			// 类
			sb.append("@HandlerEntity(mid=MID.").append(className.replace("Request", "Req_VALUE")).append(",msg=")
					.append(className).append(".class)\n");
			sb.append("public class ").append(handlerClassName).append(" extends TcpHandler {\n");
			sb.append("\tprivate static final Logger LOGGER = LoggerFactory.getLogger(").append(handlerClassName)
					.append(".class);\n\n");

			// 方法
			sb.append("\t@Override\n");
			sb.append("\tpublic void run() {\n");
			sb.append("\t\t").append(className).append(" request = getMsg();\n");
			sb.append("\t}\n");

			sb.append("}");

			if (handlerFile.exists()) {
				continue;
			}
			FileUtil.createFile(handlerFile);

			if (handlerFile.canWrite()) {
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(handlerFile, true), "UTF-8");
				osw.write(sb.toString());
				osw.flush();
				osw.close();
				info.append(handlerClassName).append("\n");
			}
		}

		return info.toString();
	}

	/**
	 * 获取子包目录
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年10月19日 下午5:25:39
	 * @param file
	 * @return
	 */
	private String getSubPackageDir(File file) {
		int beginIndex = 0;
		if (file.getName().startsWith("Hall") || file.getName().startsWith("Bydr")) {
			beginIndex = 4;
		}
		String path = file.getName().substring(beginIndex, file.getName().indexOf("Message.java"));
		return path.toLowerCase();

	}

}
