package org.sms.project.elasticsearch.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.sms.project.init.SysConfig;

/**
 * @author Sunny
 */
public enum SearchClient {

	INSTANCE;

	private TransportClient client;

	/**
	 * 读取配置文件，实例变量
	 * 
	 * @throws UnknownHostException
	 */
	public void init() throws UnknownHostException {
		Settings settings = Settings.builder().put("client.transport.sniff", false).put("transport.tcp.compress", true).build();
		client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(new TransportAddress(InetAddress.getByName(SysConfig.INSTANCE.getCacheDate("elasticsearch.host")), Integer.parseInt(SysConfig.INSTANCE.getCacheDate("elasticsearch.port"))));
	}
	
	public TransportClient getClient() {
		return client;
	}

	public void close() {
		this.client.close();
	}
}