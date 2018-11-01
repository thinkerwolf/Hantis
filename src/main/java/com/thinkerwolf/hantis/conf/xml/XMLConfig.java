package com.thinkerwolf.hantis.conf.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thinkerwolf.hantis.common.util.ClassUtils;
import com.thinkerwolf.hantis.common.util.PropertyUtils;
import com.thinkerwolf.hantis.common.util.StringUtils;
import com.thinkerwolf.hantis.datasource.jdbc.DBPoolDataSource;
import com.thinkerwolf.hantis.datasource.jdbc.DBUnpoolDataSource;
import com.thinkerwolf.hantis.datasource.jta.DBXAPoolDataSource;
import com.thinkerwolf.hantis.datasource.jta.DBXAUnpoolDataSource;
import com.thinkerwolf.hantis.session.Configuration;
import com.thinkerwolf.hantis.session.SessionFactoryBuilder;
import com.thinkerwolf.hantis.sql.SqlNode;

public class XMLConfig {

	private static final Pattern PLACE_HOLDER = Pattern.compile("\\$\\s*\\{.*\\}");
	private static final Pattern PROP_NAME = Pattern.compile("[^\\$\\s\\{\\}]+");

	private InputStream is;

	private Configuration configuration;

	public XMLConfig(InputStream is) {
		this.is = is;
		this.configuration = new Configuration();
	}

	public void parse() {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			// factory.setValidating(true);
			DocumentBuilder db = factory.newDocumentBuilder();
			Document doc = db.parse(is);

			// 解析props
			NodeList propsNodeList = doc.getElementsByTagName("props");
			for (int i = 0, len = propsNodeList.getLength(); i < len; i++) {
				parseProps((Element) propsNodeList.item(i));
			}

			Element sessionFactoriesEl = (Element) doc.getElementsByTagName("sessionFactories").item(0);

			// 解析sessionFactory
			NodeList sessionFactoryNodeList = sessionFactoriesEl.getElementsByTagName("sessionFactory");
			for (int i = 0, len = sessionFactoryNodeList.getLength(); i < len; i++) {
				parseSessionFactory((Element) sessionFactoryNodeList.item(i));
			}

			// 解析transactionManager

		} catch (Exception e) {
			throw new RuntimeException("Can't parse config, have a exception", e);
		}

	}

	private void parseProps(Element el) {
		NodeList propNode = el.getChildNodes();
		for (int i = 0, len = propNode.getLength(); i < len; i++) {
			Node node = propNode.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element propEl = (Element) node;
			configuration.getProps().setProperty(propEl.getAttribute("name").trim(),
					propEl.getAttribute("value").trim());
		}
	}

	private SessionFactoryBuilder parseSessionFactory(Element el) {
		String id = el.getAttribute("id");
		// TODO generate id
		SessionFactoryBuilder builder = new SessionFactoryBuilder();
		Element dataSourceEl = (Element) el.getElementsByTagName("dataSource").item(0);
		DataSource dataSource = parseDataSource(dataSourceEl);

		Map<String, SqlNode> sqlNodeMap = new HashMap<>();
		Element sqlsEl = (Element) el.getElementsByTagName("mappings").item(0);
		parseMappings(sqlsEl, sqlNodeMap);

		builder.setId(id);
		builder.setDataSource(dataSource);
		return builder;
	}

	private DataSource parseDataSource(Element el) {
		String dataSourceType = el.getAttribute("type");
		// POOL UNPOOL XAPOOL XAUNPOOL
		if (StringUtils.isEmpty(dataSourceType)) {
			throw new RuntimeException("dataSource type is null");
		}
		DataSource dataSource = null;
		if ("POOL".equals(dataSourceType)) {
			dataSource = new DBPoolDataSource();
		} else if ("UNPOOL".equals(dataSourceType)) {
			dataSource = new DBUnpoolDataSource();
		} else if ("XAPOOL".equals(dataSourceType)) {
			dataSource = new DBXAPoolDataSource();
		} else if ("XAUNPOOL".equals(dataSourceType)) {
			dataSource = new DBXAUnpoolDataSource();
		} else {
			dataSource = (DataSource) ClassUtils.newInstance(ClassUtils.forName(dataSourceType));
		}
		NodeList propsNl = el.getElementsByTagName("property");
		Properties props = new Properties();
		for (int i = 0, len = propsNl.getLength(); i < len; i++) {
			Element propEl = (Element) propsNl.item(i);
			props.setProperty(propEl.getAttribute("name").trim(),
					getPropertyValue(propEl.getAttribute("value").trim()));
		}
		PropertyUtils.setProperties(dataSource, props);
		return dataSource;
	}

	/**
	 * 解析sqls
	 * 
	 * @param el
	 */
	private void parseMappings(Element el, Map<String, SqlNode> sqlNodeMap) {
		NodeList nl = el.getElementsByTagName("mapping");
		for (int i = 0, len = nl.getLength(); i < len; i++) {
			Element mappingEl = (Element) nl.item(i);
			String resource = mappingEl.getAttribute("resource");
			
		}

	}

	private String getPropertyValue(String originValue) {
		if (PLACE_HOLDER.matcher(originValue).find()) {
			Matcher m = PROP_NAME.matcher(originValue);
			String propName = m.group().trim();
			originValue = configuration.getProps().getProperty(propName);
		}
		return originValue;
	}

}