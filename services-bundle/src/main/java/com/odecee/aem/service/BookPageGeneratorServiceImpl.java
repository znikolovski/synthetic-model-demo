package com.odecee.aem.service;

import java.util.Iterator;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceProvider;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.odecee.aem.database.BookResourceProvider;
import com.odecee.aem.database.impl.BookResourceProviderImpl;

@Component(
		immediate = true, 
		label = "Pages Generation Service", 
		description = "Pages Generation Service Configuration", 
		name = "com.odecee.aem.service.BookPageGeneratorServiceImpl", metatype = true)
@Service
public class BookPageGeneratorServiceImpl implements BookPageGeneratorService {

	private final static Logger log = LoggerFactory.getLogger(BookPageGeneratorServiceImpl.class);

	private static final String PROP_GENERATOR_SOURCE_PATH_DEFAULT = "/content/book-store/bookinfo/";
	private static final String PROP_GENERATOR_DESTINATION_PATH_DEFAULT = "/content/book-store/books/";
	private static final String PROP_GENERATOR_PAGE_NAME_FIELD_DEFAULT = "title";
	private static final String PROP_GENERATOR_PAGE_ATTRIBUTE1_DEFAULT = "isbn";
	private static final String PROP_GENERATOR_PAGE_ATTRIBUTE2_DEFAULT = "description";
	private static final String PROP_GENERATOR_PAGE_ATTRIBUTE3_DEFAULT = "genrename";
	private static final String PROP_GENERATOR_PAGE_ATTRIBUTE4_DEFAULT = "firstname";
	private static final String PROP_GENERATOR_PAGE_ATTRIBUTE5_DEFAULT = "lastname";
	private static final String PROP_GENERATOR_RESOURCE_TYPE_DEFAULT = "/apps/synthetic-models/renderers/bookpage";
	private static final String PROP_GENERATOR_TEMPLATE_DEFAULT = "/apps/synthetic-models/templates/bookpage";

	public static final String PROP_GENERATOR_SOURCE_PATH = "generator.source";

	@Property(value=PROP_GENERATOR_DESTINATION_PATH_DEFAULT, label="Generator Destination")
	public static final String PROP_GENERATOR_DESTINATION_PATH = "generator.destination";

	@Property(value=PROP_GENERATOR_PAGE_NAME_FIELD_DEFAULT, label="Page name from field", description="Which data source field to be used as page name")
	public static final String PROP_GENERATOR_PAGE_NAME_FIELD = "generator.page.name";

	@Property(
		value={
			PROP_GENERATOR_PAGE_ATTRIBUTE1_DEFAULT, 
			PROP_GENERATOR_PAGE_ATTRIBUTE2_DEFAULT, 
			PROP_GENERATOR_PAGE_ATTRIBUTE3_DEFAULT,
			PROP_GENERATOR_PAGE_ATTRIBUTE4_DEFAULT,
			PROP_GENERATOR_PAGE_ATTRIBUTE5_DEFAULT}, 
		label="Page attributes list", 
		description="A list of attributes to set as properties on the page")
	public static final String PROP_GENERATOR_PAGE_ATTRIBUTES = "generator.page.properties";

	@Property(value=PROP_GENERATOR_TEMPLATE_DEFAULT, label="Page template")
	public static final String PROP_GENERATOR_TEMPLATE = "generator.page.template";

	@Property(value=PROP_GENERATOR_RESOURCE_TYPE_DEFAULT, label="Resource type")
	public static final String PROP_GENERATOR_RESOURCE_TYPE = "generator.page.resource.type";

	private String sourcePath;
	private String destinationPath;
	private String pageName;
	private String[] pageAttributes;
	private String template;
	private String resourceType;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private SlingSettingsService slingSettingsService;

	private PageManager pageManager;

	@Override
	public void generatePageNodes() {
		ResourceResolver resolver = null;
		PageGenerator generator = null;

		try {
			resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Session session = resolver.adaptTo(Session.class);
			pageManager = resolver.adaptTo(PageManager.class);
			Resource sourceResource = resolver.getResource(sourcePath);
			if(sourceResource == null) {
				return;
			}
			generator = new PageGenerator(pageManager, session, pageName, pageAttributes, template, resourceType);

			generator.generateRootPath(destinationPath);
			generator.generatePages(sourceResource, destinationPath);
			session.save();

		} catch (RepositoryException e) {
			log.error(e.getLocalizedMessage());
		} catch (WCMException e) {
			log.error(e.getLocalizedMessage());
		} catch (LoginException e) {
			log.error(e.getLocalizedMessage());
		}
	}

	@Activate
	public void activate(ComponentContext context) throws Exception {
		sourcePath = PropertiesUtil.toString(context.getProperties().get(PROP_GENERATOR_SOURCE_PATH), PROP_GENERATOR_SOURCE_PATH_DEFAULT);
		destinationPath = PropertiesUtil.toString(context.getProperties().get(PROP_GENERATOR_DESTINATION_PATH), PROP_GENERATOR_DESTINATION_PATH_DEFAULT);
		pageName = PropertiesUtil.toString(context.getProperties().get(PROP_GENERATOR_PAGE_NAME_FIELD), PROP_GENERATOR_PAGE_NAME_FIELD_DEFAULT);
		pageAttributes = PropertiesUtil.toStringArray(context.getProperties().get(PROP_GENERATOR_PAGE_ATTRIBUTES));
		template = PropertiesUtil.toString(context.getProperties().get(PROP_GENERATOR_TEMPLATE), PROP_GENERATOR_TEMPLATE_DEFAULT);
		resourceType = PropertiesUtil.toString(context.getProperties().get(PROP_GENERATOR_RESOURCE_TYPE), PROP_GENERATOR_RESOURCE_TYPE_DEFAULT);

		Set<String> runModes = slingSettingsService.getRunModes();

		if(!runModes.contains("author")) {
			return;
		}

		ServiceListener sl = new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent event) {
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						generatePageNodes();
						break;
					}
					default:
						break;
				}
			}
		};

		String filter = "(objectclass=" + BookResourceProvider.class.getName() + ")";
		try {
			context.getBundleContext().addServiceListener(sl, filter);
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}


	}

}
