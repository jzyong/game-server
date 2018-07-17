package com.jzy.game.tool.client.ssl;
/**
* <h3>RSA证书生成步骤</h3>
* <p>
* //服务器证书生成
* keytool -genkey -keyalg RSA -keysize 2048 -validity 36500 -alias GATE -keypass 123456 -keystore gate.jzy -storepass 123456 -dname "CN=localhost,OU=JJY,O=CN,L=CD,ST=SC,C=CN"
* //服务器证书导出
*	keytool -export -alias GATE -file gate.cert -keystore gate.jzy -storepass 123456
*	//服务器导入客户端证书
*	keytool -import  -file client.cert -keystore gate.jzy -storepass 123456
*	//查看证书条目
*	keytool -list -v -keystore gate.jzy -storepass 123456
*	
*	//客户端证书生成
*	keytool -genkey -keyalg RSA -keysize 2048 -validity 36500 -alias CLIENT -keypass 123456 -keystore client.jzy -storepass 123456 -dname "CN=localhost,OU=JJY,O=CN,L=CD,ST=SC,C=CN"
*	//客户端证书导出
*	keytool -export -alias CLIENT -file client.cert -keystore client.jzy -storepass 123456
*	//客户端导入服务器证书
*	keytool -import  -file gate.cert -keystore client.jzy -storepass 123456
*	//查看证书条目
*	keytool -list -v -keystore client.jzy -storepass 123456
* </p>
*/

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.tool.client.PressureClientTool;

/**
 * <code>SSLContext</code>生成工厂，使用tls协议，RSA算法 <br>
 * 暂时测试用，RSA为非对称加密算法，运行速率过慢，实际运用可使用AES算法加解密
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月5日 上午11:49:53
 */
public class ClientSslContextFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSslContextFactory.class);
	private static final String PROTOCOL = "TLS"; // 协议
	private static final String KEY_MANAGER_FACTORY_ALGORITHM; // KEY管理算法名称
	private static final String GATE_KEYSTORE = "gate.keystore"; // 证书名称
	private static final String CLIENT_KEYSTORE = "client.p12"; // 证书名称
	private static final char[] GATE_PW = { '1', '2', '3', '4', '5', '6' }; // keystore密码
	private static SSLContext serverContext;
	private static SSLContext clientContext;

	static {
		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = KeyManagerFactory.getDefaultAlgorithm();
		}
		KEY_MANAGER_FACTORY_ALGORITHM = algorithm;
	}

	/**
	 * 获取 SSL上下文
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月5日 下午2:11:31
	 * @param server
	 *            true服务器模式
	 * @return
	 */
	public static SSLContext getInstance(boolean server) {
		SSLContext retInstance = null;
		if (server) {
			synchronized (ClientSslContextFactory.class) {
				if (serverContext == null) {
					try {
						serverContext = createServerSslContext();
					} catch (Exception e) {
						LOGGER.error("不能创建服务器SSLContext", e);
					}
				}
			}
			retInstance = serverContext;
		} else {
			synchronized (ClientSslContextFactory.class) {
				if (clientContext == null) {
					try {
						clientContext = createClientSslContext();
					} catch (Exception e) {
						LOGGER.error("不能创建客户端SSLContext", e);
					}
					
				}
			}
			retInstance = clientContext;
		}
		return retInstance;
	}

	/**
	 * 服务器 SSLContext
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月5日 下午2:19:01
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static SSLContext createServerSslContext() throws GeneralSecurityException, IOException {
		// Create keystore
		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream in = null;
		try {
			in = ClientSslContextFactory.class.getResourceAsStream(GATE_KEYSTORE);
			if(in==null) {
				in=FileUtil.getFileInputStream(PressureClientTool.configPath+java.io.File.separatorChar+GATE_KEYSTORE);
			}
			ks.load(in, GATE_PW);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}

		// Set up key manager factory to use our key store
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
		kmf.init(ks, GATE_PW);

		// Initialize the SSLContext to work with our key managers.
		SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		sslContext.init(kmf.getKeyManagers(), ClientTrustManagerFactory.X509_MANAGERS, null);
		return sslContext;
	}

	private static SSLContext createClientSslContext()throws GeneralSecurityException, IOException {
		// Create keystore
		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream in = null;
		try {
			in = ClientSslContextFactory.class.getResourceAsStream(CLIENT_KEYSTORE);
			if(in==null) {
				in=FileUtil.getFileInputStream(PressureClientTool.configPath+java.io.File.separatorChar+CLIENT_KEYSTORE);
			}
			ks.load(in, GATE_PW);	//TODO 密码暂时都一样
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}

		// Set up key manager factory to use our key store
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
		kmf.init(ks, GATE_PW);

		// Initialize the SSLContext to work with our key managers.
		SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		sslContext.init(kmf.getKeyManagers(), ClientTrustManagerFactory.X509_MANAGERS, null);
		return sslContext;
	}

	public static void main(String[] args) {
		SSLContext sslContext = getInstance(true);
	}
}
