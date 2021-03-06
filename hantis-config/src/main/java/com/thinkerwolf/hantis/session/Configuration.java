package com.thinkerwolf.hantis.session;

import com.thinkerwolf.hantis.common.DefaultNameHandler;
import com.thinkerwolf.hantis.common.NameHandler;
import com.thinkerwolf.hantis.common.io.Resources;
import com.thinkerwolf.hantis.conf.xml.XMLConfig;
import com.thinkerwolf.hantis.sql.xml.XmlSqlNodeParser;
import com.thinkerwolf.hantis.transaction.TransactionManager;
import com.thinkerwolf.hantis.type.TypeHandlerRegistry;

import java.io.InputStream;
import java.util.*;

/**
 * Project configuration
 *
 * @author wukai
 */
public class Configuration {
    /**
     * 全局属性
     */
    private Properties props = new Properties();

    private XmlSqlNodeParser parser = new XmlSqlNodeParser();

    private Map<String, SessionFactoryBuilder> sessionFactoryBuilders = new HashMap<>();

    private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    private NameHandler nameHandler = new DefaultNameHandler();
    
    private TransactionManager transactionManager;
    
    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public XmlSqlNodeParser getParser() {
        return parser;
    }

    public void putSessionFactoryBuilder(SessionFactoryBuilder builder) {
        sessionFactoryBuilders.putIfAbsent(builder.getId(), builder);
    }

    public List<SessionFactoryBuilder> getAllSessionFactoryBuilder() {
        return new ArrayList<>(sessionFactoryBuilders.values());
    }

    public SessionFactoryBuilder getSessionFactoryBuilder(String sfbName) {
        return sessionFactoryBuilders.get(sfbName);
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public NameHandler getNameHandler() {
        return nameHandler;
    }

    public void setNameHandler(NameHandler nameHandler) {
        this.nameHandler = nameHandler;
    }

    public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public Configuration config(String configPath) {
        try {
            return config(Resources.getResource(configPath).getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Parse config file error", e);
        }
    }

    public Configuration config(InputStream is) {
        XMLConfig xmlConfig = new XMLConfig(is, this);
        xmlConfig.parse();
        return this;
    }

}
