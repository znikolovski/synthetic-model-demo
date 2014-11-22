package com.odecee.aem.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;

/**
 * Util class for handling links.
 * 
 */
public final class LinkUtil {

    private LinkUtil() {

    }

    /**
     * Returns an URL mapped from the (resource) path.
     * 
     * @param resourceResolver
     * @param slingRequest
     * @param url
     * @return mapped URL
     */
    public static String getMappingURL(ResourceResolver resourceResolver, SlingHttpServletRequest slingRequest, String url) {
	return resourceResolver.map(slingRequest, url) != null ? resourceResolver.map(slingRequest, url) : url;
    }

    /**
     * Prepares a link - as internal or external and checks for redirects.
     * 
     * @param link
     *            the link to check and prepare.
     * @return boolean
     */
    public static String prepareLinkPath(final Page page) {
	String path = PageUtil.getPathFromPage(page);
	if (StringUtils.isNotEmpty(path)) {
	    if (isInternalLink(path)) {
		return addHtmlExtension(path);
	    } else {
		return path;
	    }
	}
	return path;
    }

    /**
     * Prepares a link - as internal or external and checks for redirects.
     * 
     * @param link
     *            the link to check and prepare.
     * @return boolean
     */
    public static String prepareLinkPath(SlingHttpServletRequest request, final String link) {
	if (StringUtils.isNotEmpty(link)) {
	    if (isInternalLink(link)) {
		String path = PageUtil.getPathFromPage(PageUtil.getPage(link, request));
		return addHtmlExtension(path);
	    } else {
		return link;
	    }
	}
	return link;
    }

    /**
     * Adds .html to the given URL/link if not yet present and if the url does not end with /.
     * 
     * @param url
     *            the url
     * @return url with .html
     */
    public static String addHtmlExtension(final String url) {
	if (StringUtils.isEmpty(url)) {
	    return url;
	}
	// External URL
	if (!isInternalLink(url)) {
	    return url;
	}

	// URL targets to the dam
	if (isLinkToDAM(url)) {
	    return url;
	}

	// URL ends with .html (ex. .html, .html?, .html#)
	if (url.matches(".+\\.html(|\\?.*|#.*)$")) {
	    return url;
	}
	return url + ".html";
    }

    /**
     * Checks if a link is an internal CQ link.
     * 
     * @param link
     *            the link to check.
     * @return boolean
     */
    public static boolean isInternalLink(final String link) {
	boolean isInternalLink = false;
	if (!link.matches("^http(s)?://.+") && link.matches(PageUtil.CONTENT + "/.+")) {
	    isInternalLink = true;
	}
	return isInternalLink;
    }

    /**
     * Check if a link points to the DAM.
     * 
     * @param link
     *            the link
     * @return true if link contains the DAM prefix
     */
    public static boolean isLinkToDAM(String link) {
	if (StringUtils.isEmpty(link)) {
	    return false;
	}
	return link.startsWith(DamConstants.MOUNTPOINT_ASSETS);
    }
}
