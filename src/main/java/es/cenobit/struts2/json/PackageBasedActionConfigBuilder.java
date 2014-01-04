/**
 * Copyright 2014 Cenobit Technologies Inc. http://cenobit.es/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package es.cenobit.struts2.json;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardHelper;
import com.opensymphony.xwork2.util.classloader.ReloadingClassLoader;
import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.ClassFinder.ClassInfo;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.finder.Test;
import com.opensymphony.xwork2.util.finder.UrlSet;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import es.cenobit.struts2.json.annotations.Json;

public class PackageBasedActionConfigBuilder implements ActionConfigBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(PackageBasedActionConfigBuilder.class);
	private static final boolean EXTRACT_BASE_INTERFACES = true;

	@SuppressWarnings("unused")
	private final Container container;
	@SuppressWarnings("unused")
	private final Configuration configuration;
	private final ObjectFactory objectFactory;

	private String[] actionPackages;
	private String[] excludePackages;
	private String[] packageLocators;
	private String[] includeJars;
	private String[] conventionIncludeJars;
	private String packageLocatorsBasePackage;
	private boolean disableActionScanning = false;
	private boolean disablePackageLocatorsScanning = false;
	private boolean checkImplementsAction = true;
	private Set<String> loadedFileUrls = new HashSet<String>();
	private boolean devMode;
	private ReloadingClassLoader reloadingClassLoader;
	private boolean reload;
	private Set<String> fileProtocols;
	private boolean excludeParentClassLoader;
	private boolean eagerLoading = false;
	private FileManager fileManager;

	@Inject
	public PackageBasedActionConfigBuilder(Configuration configuration, Container container, ObjectFactory objectFactory) {
		this.configuration = configuration;
		this.container = container;
		this.objectFactory = objectFactory;
	}

	@Inject(StrutsConstants.STRUTS_DEVMODE)
	public void setDevMode(String mode) {
		this.devMode = "true".equals(mode);
	}

	/**
	 * Reload configuration when classes change. Defaults to "false" and should
	 * not be used in production.
	 */
	@Inject("struts.json.classes.reload")
	public void setReload(String reload) {
		this.reload = "true".equals(reload);
	}

	@Inject
	public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
		this.fileManager = fileManagerFactory.getFileManager();
	}

	/**
	 * File URLs whose protocol are in these list will be processed as jars
	 * containing classes
	 * 
	 * @param fileProtocols
	 *            Comma separated list of file protocols that will be considered
	 *            as jar files and scanned
	 */
	@Inject("struts.json.action.fileProtocols")
	public void setFileProtocols(String fileProtocols) {
		if (StringUtils.isNotBlank(fileProtocols)) {
			this.fileProtocols = TextParseUtil.commaDelimitedStringToSet(fileProtocols);
		}
	}

	/**
	 * Exclude URLs found by the parent class loader. Defaults to "true", set to
	 * true for JBoss
	 */
	@Inject("struts.json.exclude.parentClassLoader")
	public void setExcludeParentClassLoader(String exclude) {
		this.excludeParentClassLoader = "true".equals(exclude);
	}

	/**
	 * @param disableActionScanning
	 *            Disable scanning for actions
	 */
	@Inject(value = "struts.json.action.disableScanning", required = false)
	public void setDisableActionScanning(String disableActionScanning) {
		this.disableActionScanning = "true".equals(disableActionScanning);
	}

	/**
	 * @param includeJars
	 *            Comma separated list of regular expressions of jars to be
	 *            included.
	 */
	@Inject(value = "struts.json.action.includeJars", required = false)
	public void setIncludeJars(String includeJars) {
		if (StringUtils.isNotEmpty(includeJars))
			this.includeJars = includeJars.split("\\s*[,]\\s*");
	}

	/**
	 * @param includeJars
	 *            Comma separated list of regular expressions of jars to be
	 *            included.
	 */
	@Inject(value = "struts.convention.action.includeJars", required = false)
	public void setConventionIncludeJars(String includeJars) {
		if (StringUtils.isNotEmpty(includeJars))
			this.conventionIncludeJars = includeJars.split("\\s*[,]\\s*");
	}

	/**
	 * @param disablePackageLocatorsScanning
	 *            If set to true, only the named packages will be scanned
	 */
	@Inject(value = "struts.json.package.locators.disable", required = false)
	public void setDisablePackageLocatorsScanning(String disablePackageLocatorsScanning) {
		this.disablePackageLocatorsScanning = "true".equals(disablePackageLocatorsScanning);
	}

	/**
	 * @param actionPackages
	 *            (Optional) An optional list of action packages that this
	 *            should create configuration for.
	 */
	@Inject(value = "struts.json.action.packages", required = false)
	public void setActionPackages(String actionPackages) {
		if (StringUtils.isNotBlank(actionPackages)) {
			this.actionPackages = actionPackages.split("\\s*[,]\\s*");
		}
	}

	/**
	 * @param checkImplementsAction
	 *            (Optional) Map classes that implement
	 *            com.opensymphony.xwork2.Action as actions
	 */
	@Inject(value = "struts.json.action.checkImplementsAction", required = false)
	public void setCheckImplementsAction(String checkImplementsAction) {
		this.checkImplementsAction = "true".equals(checkImplementsAction);
	}

	/**
	 * @param excludePackages
	 *            (Optional) A list of packages that should be skipped when
	 *            building configuration.
	 */
	@Inject(value = "struts.json.exclude.packages", required = false)
	public void setExcludePackages(String excludePackages) {
		if (StringUtils.isNotBlank(excludePackages)) {
			this.excludePackages = excludePackages.split("\\s*[,]\\s*");
		}
	}

	/**
	 * @param packageLocators
	 *            (Optional) A list of names used to find action packages.
	 */
	@Inject(value = "struts.json.package.locators", required = false)
	public void setPackageLocators(String packageLocators) {
		this.packageLocators = packageLocators.split("\\s*[,]\\s*");
	}

	/**
	 * @param packageLocatorsBasePackage
	 *            (Optional) If set, only packages that start with this name
	 *            will be scanned for actions.
	 */
	@Inject(value = "struts.json.package.locators.basePackage", required = false)
	public void setPackageLocatorsBase(String packageLocatorsBasePackage) {
		this.packageLocatorsBasePackage = packageLocatorsBasePackage;
	}

	@Override
	public void buildActionConfigs() {
		// setup reload class loader based on dev settings
		initReloadClassLoader();

		if (!disableActionScanning) {
			if (actionPackages == null && packageLocators == null) {
				throw new ConfigurationException("At least a list of action packages or action package locators "
						+ "must be given using one of the properties [struts.json.action.packages] or "
						+ "[struts.json.package.locators]");
			}

			if (LOG.isTraceEnabled()) {
				LOG.trace("Loading action configurations");
				if (actionPackages != null)
					LOG.trace("Actions being loaded from action packages " + Arrays.asList(actionPackages));
				if (packageLocators != null)
					LOG.trace("Actions being loaded using package locators " + Arrays.asList(packageLocators));
				if (excludePackages != null)
					LOG.trace("Excluding actions from packages " + Arrays.asList(excludePackages));
			}

			@SuppressWarnings("rawtypes")
			Set<Class> classes = findActions();
			buildConfiguration(classes);
		}
	}

	protected void buildConfiguration(@SuppressWarnings("rawtypes") Set<Class> classes) {

		for (Class<?> actionClass : classes) {
			Json jsonAnnotation = actionClass.getAnnotation(Json.class);

			// Skip classes that can't be instantiated or don't annotated with
			// @Json
			if (cannotInstantiate(actionClass) && jsonAnnotation == null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Class [#0] did not pass the instantiation test and will be ignored",
							actionClass.getName());
				}
				continue;
			}

			if (eagerLoading) {
				// Tell the ObjectFactory about this class
				try {
					objectFactory.getClassInstance(actionClass.getName());
				} catch (ClassNotFoundException e) {
					if (LOG.isErrorEnabled()) {
						LOG.error("Object Factory was unable to load class [#0]", e, actionClass.getName());
					}
					throw new StrutsException("Object Factory was unable to load class " + actionClass.getName(), e);
				}
			}

			String actionPackage = actionClass.getPackage().getName();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Processing class [#0] in package [#1]", actionClass.getName(), actionPackage);
			}

			String defaultActionName = actionClass.getSimpleName();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Processing defaultActionName [#0] in package [#1]", defaultActionName, actionPackage);
			}

			// Verify that the annotations have no errors and also determine
			// if the default action
			// configuration should still be built or not.
			@SuppressWarnings("unused")
			Map<String, List<Json>> map = getActionAnnotations(actionClass);

			// TODO: Create action config

		}
	}

	/**
	 * Locates all of the {@link Json} annotations on methods within the Action
	 * class and its parent classes.
	 * 
	 * @param actionClass
	 *            The action class.
	 * @return The list of annotations or an empty list if there are none.
	 */
	protected Map<String, List<Json>> getActionAnnotations(Class<?> actionClass) {
		Method[] methods = actionClass.getMethods();
		Map<String, List<Json>> map = new HashMap<String, List<Json>>();
		for (Method method : methods) {
			Json ann = method.getAnnotation(Json.class);
			if (ann != null) {
				map.put(method.getName(), Arrays.asList(ann));
			}
		}

		return map;
	}

	/**
	 * Interfaces, enums, annotations, and abstract classes cannot be
	 * instantiated.
	 * 
	 * @param actionClass
	 *            class to check
	 * @return returns true if the class cannot be instantiated or should be
	 *         ignored
	 */
	protected boolean cannotInstantiate(Class<?> actionClass) {
		return actionClass.isAnnotation() || actionClass.isInterface() || actionClass.isEnum()
				|| (actionClass.getModifiers() & Modifier.ABSTRACT) != 0 || actionClass.isAnonymousClass();
	}

	@SuppressWarnings("rawtypes")
	protected Set<Class> findActions() {
		Set<Class> classes = new HashSet<Class>();
		try {
			if (actionPackages != null || (packageLocators != null && !disablePackageLocatorsScanning)) {

				// By default, ClassFinder scans EVERY class in the specified
				// url set, which can produce spurious warnings for non-action
				// classes that can't be loaded. We pass a package filter that
				// only considers classes that match the action packages
				// specified by the user
				Test<String> classPackageTest = getClassPackageTest();
				List<URL> urls = readUrls();
				ClassFinder finder = new ClassFinder(getClassLoaderInterface(), urls, EXTRACT_BASE_INTERFACES,
						fileProtocols, classPackageTest);

				Test<ClassFinder.ClassInfo> test = getActionClassTest();
				classes.addAll(finder.findClasses(test));
			}
		} catch (Exception ex) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Unable to scan named packages", ex);
			}
		}

		return classes;
	}

	/**
	 * Note that we can't include the test for {@link #actionSuffix} here
	 * because a class is included if its name ends in {@link #actionSuffix} OR
	 * it implements {@link com.opensymphony.xwork2.Action}. Since the whole
	 * goal is to avoid loading the class if we don't have to, the (actionSuffix
	 * || implements Action) test will have to remain until later. See
	 * {@link #getActionClassTest()} for the test performed on the loaded
	 * {@link ClassInfo} structure.
	 * 
	 * @param className
	 *            the name of the class to test
	 * @return true if the specified class should be included in the
	 *         package-based action scan
	 */
	protected boolean includeClassNameInActionScan(String className) {
		String classPackageName = StringUtils.substringBeforeLast(className, ".");
		return (checkActionPackages(classPackageName) || checkPackageLocators(classPackageName))
				&& checkExcludePackages(classPackageName);
	}

	/**
	 * Checks if provided class package is on the exclude list
	 * 
	 * @param classPackageName
	 *            name of class package
	 * @return false if class package is on the {@link #excludePackages} list
	 */
	protected boolean checkExcludePackages(String classPackageName) {
		if (excludePackages != null && excludePackages.length > 0) {
			WildcardHelper wildcardHelper = new WildcardHelper();

			// we really don't care about the results, just the boolean
			Map<String, String> matchMap = new HashMap<String, String>();

			for (String packageExclude : excludePackages) {
				int[] packagePattern = wildcardHelper.compilePattern(packageExclude);
				if (wildcardHelper.match(matchMap, classPackageName, packagePattern)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if class package match provided list of action packages
	 * 
	 * @param classPackageName
	 *            name of class package
	 * @return true if class package is on the {@link #actionPackages} list
	 */
	protected boolean checkActionPackages(String classPackageName) {
		if (actionPackages != null) {
			for (String packageName : actionPackages) {
				String strictPackageName = packageName + ".";
				if (classPackageName.equals(packageName) || classPackageName.startsWith(strictPackageName))
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks if class package match provided list of package locators
	 * 
	 * @param classPackageName
	 *            name of class package
	 * @return true if class package is on the {@link #packageLocators} list
	 */
	protected boolean checkPackageLocators(String classPackageName) {
		if (packageLocators != null && !disablePackageLocatorsScanning && classPackageName.length() > 0
				&& (packageLocatorsBasePackage == null || classPackageName.startsWith(packageLocatorsBasePackage))) {
			for (String packageLocator : packageLocators) {
				String[] splitted = classPackageName.split("\\.");

				if (es.cenobit.struts2.json.util.StringUtils.contains(splitted, packageLocator, false))
					return true;
			}
		}
		return false;
	}

	/**
	 * Construct a {@link Test} object that determines if a specified class name
	 * should be included in the package scan based on the clazz's package name.
	 * Note that the goal is to avoid loading the class, so the test should only
	 * rely on information in the class name itself. The default implementation
	 * is to return the result of {@link #includeClassNameInActionScan(String)}.
	 * 
	 * @return a {@link Test} object that returns true if the specified class
	 *         name should be included in the package scan
	 */
	protected Test<String> getClassPackageTest() {
		return new Test<String>() {
			public boolean test(String className) {
				return includeClassNameInActionScan(className);
			}
		};
	}

	/**
	 * Construct a {@link Test} Object that determines if a specified class
	 * should be included in the package scan based on the full
	 * {@link ClassInfo} of the class. At this point, the class has been loaded,
	 * so it's ok to perform tests such as checking annotations or looking at
	 * interfaces or super-classes of the specified class.
	 * 
	 * @return a {@link Test} object that returns true if the specified class
	 *         should be included in the package scan
	 */
	protected Test<ClassFinder.ClassInfo> getActionClassTest() {
		return new Test<ClassFinder.ClassInfo>() {
			public boolean test(ClassFinder.ClassInfo classInfo) {

				// Why do we call includeClassNameInActionScan here, when it's
				// already been called to in the initial call to ClassFinder?
				// When some action class passes our package filter in that
				// step,
				// ClassFinder automatically includes parent classes of that
				// action,
				// such as com.opensymphony.xwork2.ActionSupport. We repeat the
				// package filter here to filter out such results.
				boolean inPackage = includeClassNameInActionScan(classInfo.getName());

				try {
					return inPackage
							&& (checkImplementsAction && com.opensymphony.xwork2.Action.class
									.isAssignableFrom(classInfo.get()));
				} catch (ClassNotFoundException ex) {
					if (LOG.isErrorEnabled())
						LOG.error("Unable to load class [#0]", ex, classInfo.getName());
					return false;
				}
			}
		};
	}

	private List<URL> readUrls() throws IOException {
		List<URL> resourceUrls = new ArrayList<URL>();
		// Usually the "classes" dir.
		ArrayList<URL> classesList = Collections.list(getClassLoaderInterface().getResources(""));
		for (URL url : classesList) {
			resourceUrls.addAll(fileManager.getAllPhysicalUrls(url));
		}
		return buildUrlSet(resourceUrls).getUrls();
	}

	private UrlSet buildUrlSet(List<URL> resourceUrls) throws IOException {
		ClassLoaderInterface classLoaderInterface = getClassLoaderInterface();
		UrlSet urlSet = new UrlSet(resourceUrls);
		urlSet = urlSet.include(new UrlSet(classLoaderInterface, this.fileProtocols));

		// excluding the urls found by the parent class loader is desired, but
		// fails in JBoss (all urls are removed)
		if (excludeParentClassLoader) {
			// exclude parent of classloaders
			ClassLoaderInterface parent = classLoaderInterface.getParent();
			// if reload is enabled, we need to step up one level, otherwise the
			// UrlSet will be empty
			// this happens because the parent of the realoding class loader is
			// the web app classloader
			if (parent != null && isReloadEnabled())
				parent = parent.getParent();

			if (parent != null)
				urlSet = urlSet.exclude(parent);

			try {
				// This may fail in some sandboxes, ie GAE
				ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
				urlSet = urlSet.exclude(new ClassLoaderInterfaceDelegate(systemClassLoader.getParent()));

			} catch (SecurityException e) {
				if (LOG.isWarnEnabled())
					LOG.warn("Could not get the system classloader due to security constraints, there may be improper urls left to scan");
			}
		}

		// try to find classes dirs inside war files
		urlSet = urlSet.includeClassesUrl(classLoaderInterface, new UrlSet.FileProtocolNormalizer() {
			public URL normalizeToFileProtocol(URL url) {
				return fileManager.normalizeToFileProtocol(url);
			}
		});

		urlSet = urlSet.excludeJavaExtDirs();
		urlSet = urlSet.excludeJavaEndorsedDirs();
		try {
			urlSet = urlSet.excludeJavaHome();
		} catch (NullPointerException e) {
			// This happens in GAE since the sandbox contains no java.home
			// directory
			if (LOG.isWarnEnabled())
				LOG.warn("Could not exclude JAVA_HOME, is this a sandbox jvm?");
		}
		urlSet = urlSet.excludePaths(System.getProperty("sun.boot.class.path", ""));
		urlSet = urlSet.exclude(".*/JavaVM.framework/.*");

		String[] localIncludeJars = es.cenobit.struts2.json.util.StringUtils.concat(includeJars, conventionIncludeJars);

		if (localIncludeJars == null) {
			urlSet = urlSet.exclude(".*?\\.jar(!/|/)?");
		} else {
			// jar urls regexes were specified
			List<URL> rawIncludedUrls = urlSet.getUrls();
			Set<URL> includeUrls = new HashSet<URL>();
			boolean[] patternUsed = new boolean[localIncludeJars.length];

			for (URL url : rawIncludedUrls) {
				if (fileProtocols.contains(url.getProtocol())) {
					// it is a jar file, make sure it macthes at least a url
					// regex
					for (int i = 0; i < localIncludeJars.length; i++) {
						String includeJar = localIncludeJars[i];
						if (Pattern.matches(includeJar, url.toExternalForm())) {
							includeUrls.add(url);
							patternUsed[i] = true;
							break;
						}
					}
				} else {
					// it is not a jar
					includeUrls.add(url);
				}
			}

			if (LOG.isWarnEnabled()) {
				for (int i = 0; i < patternUsed.length; i++) {
					if (!patternUsed[i]) {
						LOG.warn("The includeJars pattern [#0] did not match any jars in the classpath",
								localIncludeJars[i]);
					}
				}
			}
			return new UrlSet(includeUrls);
		}

		return urlSet;
	}

	protected ClassLoaderInterface getClassLoaderInterface() {
		if (isReloadEnabled()) {
			return new ClassLoaderInterfaceDelegate(this.reloadingClassLoader);
		} else {
			/*
			 * if there is a ClassLoaderInterface in the context, use it,
			 * otherwise default to the default ClassLoaderInterface (a wrapper
			 * around the current thread classloader) using this, other plugins
			 * (like OSGi and Convention) can plugin their own classloader for a
			 * while and it will be used by Json (it cannot be a bean, as Json
			 * is likely to be called multiple times, and it needs to use the
			 * default ClassLoaderInterface during normal startup)
			 */
			ClassLoaderInterface classLoaderInterface = null;
			ActionContext ctx = ActionContext.getContext();
			if (ctx != null) {
				classLoaderInterface = (ClassLoaderInterface) ctx.get(ClassLoaderInterface.CLASS_LOADER_INTERFACE);
			}

			return ObjectUtils.defaultIfNull(classLoaderInterface, new ClassLoaderInterfaceDelegate(getClassLoader()));
		}
	}

	protected void initReloadClassLoader() {
		// when the configuration is reloaded, a new classloader will be setup
		if (isReloadEnabled() && reloadingClassLoader == null) {
			reloadingClassLoader = new ReloadingClassLoader(getClassLoader());
		}
	}

	protected ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	protected boolean isReloadEnabled() {
		return devMode && reload;
	}

	@Override
	public boolean needsReload() {
		if (devMode && reload) {
			for (String url : loadedFileUrls) {
				if (fileManager.fileNeedsReloading(url)) {
					if (LOG.isDebugEnabled())
						LOG.debug("File [#0] changed, configuration will be reloaded", url);
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public void destroy() {
		loadedFileUrls.clear();
	}

}
