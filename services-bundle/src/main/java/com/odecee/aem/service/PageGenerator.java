package com.odecee.aem.service;

import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

public class PageGenerator {
	
	private static final String SLING_RESOURCE_TYPE = "sling:resourceType";
	
	private PageManager pageManager;
	private Session session;
	private String pageNameField;
	private String[] pageAttributes;
	private String pageTemplate;
	private String resourceType;
	
	public PageGenerator(PageManager pageManager, Session session, String pageNameField, String[] pageAttributes, String pageTemplate, String resourceType) {
		this.pageManager = pageManager;
		this.session = session;
		this.pageNameField = pageNameField;
		this.pageAttributes = pageAttributes;
		this.pageTemplate = pageTemplate;
		this.resourceType = resourceType;
	}
	
	public void generateRootPath(String rootPath) throws RepositoryException {
		generatePath(rootPath);
	}
	
	private void generatePath(String path) throws RepositoryException {
		if(!session.nodeExists(path)) {
			JcrUtil.createPath(path, "sling:OrderedFolder", session);
		}
	}
	
	public void generatePages(Resource source, String destinationPath) throws WCMException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		for(Iterator<Resource> resources = source.listChildren(); resources.hasNext();) {
			Resource resource = resources.next();
			resource = resource.getResourceResolver().resolve(resource.getPath());
			generateResourcePage(resource, destinationPath);
		}
	}

	private void generateResourcePage(Resource resource, String destinationPath) throws WCMException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		String title = PropertiesUtil.toString(resource.getResourceMetadata().get(pageNameField), null);
		
		if(StringUtils.isEmpty(title)) {
			return;
		}
		
		String pageName = JcrUtil.createValidName(title);
		if(!destinationPath.endsWith("/")) {
			destinationPath = destinationPath + "/";
		}
		String pagePath = destinationPath + pageName;
		Page assetPage = pageManager.getPage(pagePath);
		
		if(assetPage == null) {
			assetPage = pageManager.create(destinationPath, pageName, pageTemplate, title);
			
			String assetPagePath = assetPage.getContentResource().getPath();
			Node assetContentNode = session.getNode(assetPagePath);
			assetContentNode.setProperty(SLING_RESOURCE_TYPE, resourceType);
			for(String attribute : pageAttributes) {
				assetContentNode.setProperty(attribute, PropertiesUtil.toString(resource.getResourceMetadata().get(attribute), ""));
			}
		}
	}

}
