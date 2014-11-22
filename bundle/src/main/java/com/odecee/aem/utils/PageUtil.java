package com.odecee.aem.utils;

import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Util class for handling pages.
 * 
 */
public final class PageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtil.class);
    
    public static final String PROPERTY_REDIRECTTARGET = "redirectTarget";
    public static final String NO_TITLE_SET = "[no title set]";
    public static final String CQ_TEMPLATE = "cq:template";
    public static final String CONTENT = "/content";

    private PageUtil() {

    }

    /**
     * Gets the page for a path.
     * 
     * @param request
     *            Request
     * @param path
     *            path
     * @return Page of the resource of the request
     */
    public static Page getContainingPageFromPath(final SlingHttpServletRequest request, final String path) {
	Page page = null;
	final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
	if (pageManager != null) {
	    page = pageManager.getContainingPage(path);
	}
	return page;
    }

    /**
     * Gets the path of a page.
     * 
     * @param page
     * @return page path
     */
    public static String getPathFromPage(final Page page) {
	String result= "";
	if (page != null) {
	    result = page.getProperties().get(PROPERTY_REDIRECTTARGET, page.getPath());
	}
	return result;
    }

    /**
     * Converts a handle into a page, if possible.
     * 
     * @param handle
     *            the page handle
     * @param slingRequest
     *            the request, needed for resource resolving
     * @return the page or null
     */
    public static Page getPage(final String handle, final SlingHttpServletRequest slingRequest) {
	Page page = null;
	if (StringUtils.isNotEmpty(handle)) {
	    final Resource rootResource = slingRequest.getResourceResolver().getResource(handle);
	    if (isValidResource(rootResource)) {
		page = rootResource.adaptTo(Page.class);
	    }
	}
	return page;
    }

    /**
     * Returns if the resource is an existing resource.
     * 
     * @param resource
     *            the resource
     * @return false if resource is null or instance of NonExistingResource
     */
    public static boolean isValidResource(final Resource resource) {
	return resource != null && !(resource instanceof NonExistingResource);
    }

    /**
     * Returns the page title. If the page title is <code>null</code> or empty, the jcr:titel-Property of the page is returned
     * 
     * @param page
     * @return page title
     */
    public static String getPageTitleFromPage(final Page page) {
	String title = page.getPageTitle();
	if (title == null) {
	    title = getJcrTitleFromPage(page);
	}
	return title;
    }

    /**
     * Returns the jcr:title property of the page.
     * 
     * @param page
     * @return jcr title
     */
    public static String getJcrTitleFromPage(final Page page) {
	String title = page.getTitle();
	if (title == null) {
	    title = page.getName();
	}
	if (title == null) {
	    title = NO_TITLE_SET;
	}
	return title;
    }

    /**
     * Returns the page navigation title for a product page. 
     * 
     * @param page
     * @return navigation title
     */
    public static String getNavigationTitleFromProductPage(final Page page) {
	return page.getNavigationTitle() != null ? page.getNavigationTitle() : "";
    }
 
    /**
     * Returns the page navigation title. If the navigation title is <code>null</code> or empty, we get the the jcr:titel-Property of the page is returned
     * 
     * @param page
     * @return navigation title
     */
    public static String getNavigationTitleFromPage(final Page page) {
	String title = page.getNavigationTitle();
	if (title == null) {
	    title = getJcrTitleFromPage(page);
	}
	return title;
    }

    /**
     * Gets the value of a property as string.
     * 
     * If the property is not available null will be returned.
     * 
     * @param node
     *            to get property from
     * @param propertyName
     *            name of the property
     * @return value of property as string.
     */
    public static String getValueStringFromProperty(final Node node, final String propertyName) {
	String valueString = null;
	try {
	    if (node != null && node.hasProperty(propertyName)) {
		final Value value = node.getProperty(propertyName).getValue();
		if (value != null) {
		    valueString = StringUtils.isEmpty(value.getString()) ? null : value.getString();
		}
	    }
	} catch (RepositoryException e) {
	    LOG.warn("Unable to get Property: " + propertyName + " on node: " + node.toString() + "." + e);
	}
	return valueString;
    }

    /**
     * Gets the path to the template of a page.
     * 
     * @param page
     * @return template path
     */
    public static String getTemplatePathFromPage(final Page page) {
	if (page == null) {
	    return StringUtils.EMPTY;
	}
	final ValueMap properties = page.getProperties();
	if (properties != null && properties.containsKey(CQ_TEMPLATE)) {
	    return (String) properties.get(CQ_TEMPLATE);
	}
	return StringUtils.EMPTY;
    }

    /**
     * Gets the name of the template of a page.
     * 
     * @param page
     * @return template name
     */
    public static String getTemplateNameFromPage(final Page page) {
	final String templatePath = PageUtil.getTemplatePathFromPage(page);
	if (StringUtils.isBlank(templatePath)) {
	    return StringUtils.EMPTY;
	}
	final String[] pathSplit = templatePath.split(Pattern.quote("/"));
	return pathSplit[pathSplit.length - 1];
    }

    /**
     * Gets the jcr:content node of a page.
     * 
     * @param page
     * @return content node
     */
    public static Node getJcrNodeFromPage(final Page page) {
	Node node = page.adaptTo(Node.class);
	try {
	    node = node.getNode(JcrConstants.JCR_CONTENT);
	} catch (PathNotFoundException e) {
	    LOG.warn("Error while trying to get jcr:content Node from Page [" + page.getPath() + "]", e);
	} catch (RepositoryException e) {
	    LOG.warn("Error while trying to get jcr:content Node from Page [" + page.getPath() + "]", e);
	}
	return node;
    }

}
