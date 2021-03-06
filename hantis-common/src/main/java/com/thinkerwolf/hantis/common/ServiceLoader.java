package com.thinkerwolf.hantis.common;

import com.thinkerwolf.hantis.common.log.InternalLoggerFactory;
import com.thinkerwolf.hantis.common.log.Logger;
import com.thinkerwolf.hantis.common.util.ClassUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T>
 * @author wukai
 */
public class ServiceLoader<T> {

    public static final String SERVICES_FOLDER = "META-INF/services/";

    private static final Logger logger = InternalLoggerFactory.getLogger(ServiceLoader.class);

    /**
     * service loader map
     */
    private static Map<Class<?>, ServiceLoader<?>> serviceLoaderMap = new ConcurrentHashMap<>();

    private Map<String, T> serviceMap = new ConcurrentHashMap<String, T>();

    @SuppressWarnings("unchecked")
    private static <T> ServiceLoader<T> load(Class<T> service, ClassLoader cl) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Load service " + SERVICES_FOLDER + service.getName());
            }
            Enumeration<URL> urls = ClassLoader.getSystemResources(SERVICES_FOLDER + service.getName());
            ServiceLoader<T> sl = (ServiceLoader<T>) serviceLoaderMap.get(service);
            if (sl == null) {
                sl = new ServiceLoader<>();
            }
            cl = cl == null ? ClassUtils.getDefaultClassLoader() : cl;
            for (; urls.hasMoreElements(); ) {
                URL url = urls.nextElement();
                Properties pros = new Properties();
                pros.load(url.openStream());
                Enumeration<Object> keys = pros.keys();
                for (; keys.hasMoreElements(); ) {
                    String key = String.valueOf(keys.nextElement()).trim();
                    String serviceName = pros.getProperty(key).trim();
                    sl.serviceMap.putIfAbsent(key, loadService(service, serviceName, cl));
                }
            }

            return sl;
        } catch (IOException e) {
            throw new ServiceConfigurationError("load error", e);
        }
    }

    private static <T> T loadService(Class<T> service, String serviceName, ClassLoader cl) {
        Class<?> c;
        try {
            c = Class.forName(serviceName, false, cl);
        } catch (ClassNotFoundException e) {
            throw new ServiceConfigurationError("class not found", e);
        }
        if (!service.isAssignableFrom(c)) {
            throw new ServiceConfigurationError(serviceName + " not a subtype of " + service.getName());
        }
        try {
            return service.cast(c.newInstance());
        } catch (Exception e) {
            throw new ServiceConfigurationError(serviceName + " initilize fail");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(String name, Class<T> service) {
       // name = name.toUpperCase();
        ServiceLoader<T> loader = (ServiceLoader <T>) serviceLoaderMap.get(service);
        if (loader == null) {
            synchronized (serviceLoaderMap) {
                if (loader == null) {
                    ClassLoader cl = ClassUtils.getDefaultClassLoader();
                    loader = load(service, cl);
                    serviceLoaderMap.putIfAbsent(service, loader);
                }
            }
        }
        Object obj = loader.serviceMap.get(name);

        if (obj == null) {
            obj = ClassUtils.newInstance(name);
            if (!service.isAssignableFrom(obj.getClass())) {
                throw new ServiceConfigurationError(name + " not a subtype of " + service.getName());
            }
            loader.serviceMap.put(name, (T) obj);
        }

        if (obj == null) {
            throw new ServiceConfigurationError(service.getName() + ": [" + name + "] service is not found");
        }
        return (T) obj;
    }
    
    public static <T> T getDefaultService(Class<T> service) {
    	SLI sli = service.getAnnotation(SLI.class);
    	return getService(sli.value(), service);
    }
    

}
