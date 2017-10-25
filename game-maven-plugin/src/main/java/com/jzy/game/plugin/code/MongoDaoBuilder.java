package com.jzy.game.plugin.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.StringUtil;

/**
 * Mongodb dao代码生成插件 <br>
 * 根据实体类生成dao,用字符串拼接实现，可以用freemaker模板生成
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月18日 下午5:23:30
 */
@Mojo(name = "mongoDao", defaultPhase = LifecyclePhase.CLEAN)
public class MongoDaoBuilder extends AbstractMojo {

	/**
	 * 项目根目录 可以在命令行中由-D参数传入
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

	/** 实体类相对路径 */
	@Parameter(defaultValue = "/com/jjy/game/model/mongo")
	private String entityPath;

	public void execute() throws MojoExecutionException, MojoFailureException {
		File mongoDir = new File(sourceDirectory.getPath() + entityPath);
		getLog().info("mongo文件夹:" + mongoDir.getPath());
		List<File> entityFiles = new ArrayList<File>();
		getEntityFile(entityFiles, mongoDir);
		for (File file : entityFiles) {
			try {
				getLog().info(createMongoDao(file));
			} catch (Exception e) {
				getLog().error(String.format("生成%s dao失败", file.getName()), e);
			}

		}
	}

	/**
	 * 创建DAO实体类
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年10月19日 上午11:13:46
	 * @param file
	 * @return
	 */
	private String createMongoDao(File file) throws Exception {
		String entityPath = file.getPath();
		getLog().info("实体类路径:" + entityPath);
		String daoPath = entityPath.substring(0, entityPath.indexOf("entity")) + "dao" + File.separatorChar
				+ file.getName().substring(0, file.getName().indexOf(".")) + "Dao.java";
		getLog().info("DAO类路径:" + daoPath);
		File daoFile = new File(daoPath);
		if (daoFile.exists()) {
			return String.format("%s 类已经存在", daoFile.getName());
		}
		FileUtil.createFile(daoFile);
		// 生成类文件
		StringBuilder sb = new StringBuilder();
		sb.append("/**").append("工具生成，请遵循规范。\n @author JiangZhiYong\n*/\n");
		
		String className = file.getName().substring(0,file.getName().indexOf(".")) + "Dao"; //类名
		String classObject=StringUtil.lowerFirstChar(className);	//类对象
		String entityClassName=file.getName().substring(0,file.getName().indexOf("."));

		// 包路径
		String packagePath = daoPath.substring(daoPath.indexOf("com"), daoPath.lastIndexOf("\\"));
		packagePath = packagePath.replaceAll("\\\\", ".");
		sb.append("package ").append(packagePath).append(";\n\n");

		// 依赖类
		sb.append("import java.util.List;\n");
		sb.append("import org.mongodb.morphia.dao.BasicDAO;\n");
		sb.append("import com.jzy.game.engine.mongo.AbsMongoManager;\n");
		sb.append("import ").append(packagePath.replace("dao", "entity")).append(".").append(entityClassName)
				.append(";\n\n");

		// 类定义
		sb.append("public class ").append(className).append(" extends BasicDAO<").append(entityClassName).append(", Integer> {\n");
		sb.append("\t").append("private static volatile ").append(className).append(" ")
				.append(classObject).append(" = null;\n\n");
		
		//构造方法
		sb.append("\tpublic ").append(className).append("(AbsMongoManager mongoManager) {\n");
		sb.append("\t\tsuper(").append(file.getName().substring(0, file.getName().indexOf("."))).append(
				".class, mongoManager.getMongoClient(), mongoManager.getMorphia(),mongoManager.getMongoConfig().getDbName());\n");
		sb.append("\t}\n\n");
		
		//单例
		sb.append("\tpublic static ").append(className).append(" getDB(AbsMongoManager mongoManager) {\n");
		sb.append("\t\tif(").append(classObject).append(" == null) {\n");
		sb.append("\t\t\tsynchronized (").append(className).append(".class){\n");
		sb.append("\t\t\t\tif(").append(classObject).append(" == null){\n");
		sb.append("\t\t\t\t\t").append(classObject).append(" = new ").append(className).append("(mongoManager);\n");
		sb.append("\t\t\t\t\t}\n");
		sb.append("\t\t\t\t}\n");
		sb.append("\t\t\t}\n");
		sb.append("\t\treturn ").append(classObject).append(";\n");
		sb.append("\t}\n\n");
		
		//获取所有数据列表
		sb.append("\tpublic static List<").append(entityClassName).append("> getAll(){\n");
		sb.append("\t\t return ").append(classObject).append(".createQuery().asList();\n");
		sb.append("\t}\n\n");
		sb.append("}");
		
		if (daoFile.canWrite()) {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(daoFile, true), "UTF-8");
			osw.write(sb.toString());
			osw.flush();
			osw.close();
		}

		return sb.toString();
	}

	/**
	 * 获得entity实体对象
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年10月19日 上午11:07:07
	 * @param files
	 * @param file
	 */
	private void getEntityFile(List<File> files, File file) {
		if (!file.exists() || file.getPath().contains("dao")) {
			return;
		}
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				getEntityFile(files, f);
			}
		} else {
			if (file.getPath().contains("entity")) { // 实体类必须在entity目录下
				files.add(file);
			}

		}
	}

}
