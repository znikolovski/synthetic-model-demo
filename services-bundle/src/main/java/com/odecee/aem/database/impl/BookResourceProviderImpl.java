package com.odecee.aem.database.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceProvider;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.odecee.aem.database.BookResourceProvider;
import com.odecee.aem.service.DatabaseConnectionFactory;
import com.odecee.aem.service.DatabaseConnectionFactory.RowMapper;

@Component(metatype = false)
@Service
@Properties({
	@Property(name="service.description", value="A DB backed Book Resource"),
	@Property(name="service.vendor", value="Odecee Pty Ltd"),
	@Property(name=ResourceProvider.ROOTS, value="/content/book-store/bookinfo"),
	@Property(name=SlingConstants.PROPERTY_RESOURCE_TYPE, value="/apps/synthetic-models/components/book/book.jsp")
})
public class BookResourceProviderImpl implements BookResourceProvider {

	private final static Logger log = LoggerFactory.getLogger(BookResourceProviderImpl.class);

	@Reference
	DatabaseConnectionFactory connectionFactory;

	protected String providerRoot;

	protected String providerRootPrefix;

	protected String resourceType;

	@Activate
	protected void activate(BundleContext bundleContext, Map<?, ?> props) {
		providerRoot = props.get(ROOTS).toString();
		resourceType = props.get(SlingConstants.PROPERTY_RESOURCE_TYPE).toString();
		providerRootPrefix = providerRoot.concat("/");
	}

	@Override
	public Resource getResource(ResourceResolver resourceResolver, String path) {
		if( providerRoot.equals(path) || providerRootPrefix.equals(path) ) {
			return new SyntheticResource(resourceResolver, path, "nt:folder");
		} else if ( path.startsWith(providerRootPrefix) && isNumber(path.substring(providerRootPrefix.length()))) {
			String query = prepareSql();
			List<Resource> resources = connectionFactory.runQuery(query, path, resourceResolver, resourceType, path.substring(providerRootPrefix.length()));

			return resources.size() == 1 ? resources.get(0) : null;
		}

		return null;
	}

	@Override
	public Resource getResource(ResourceResolver resourceResolver, HttpServletRequest request, String paramString) {
		return getResource(resourceResolver, paramString);
	}

	@Override
	public Iterator<Resource> listChildren(final Resource paramResource) {
		if( providerRoot.equals(paramResource.getPath()) ) {
			String query = prepareChildrenSql();
			List<Resource> resources = connectionFactory.runQuery(query, new RowMapper<Resource>() {
				public Resource mapRow(ResultSet rs) throws SQLException {
					return new SyntheticResource(paramResource.getResourceResolver(), providerRootPrefix+rs.getString(1), resourceType);
				}
			});

			return resources.iterator();
		}

		return null;
	}

	private boolean isNumber(String numberCandidate) {
		return Pattern.matches("^\\d*$", numberCandidate);
	}

	protected String prepareSql() {
		return "SELECT "
				+ "book.\"ID\", book.\"ISBN\", book.\"TITLE\", book.\"DESCRIPTION\", "
				+ "book.\"AUTHOR_ID\" as authorId, author.\"FIRST_NAME\" as firstName, author.\"LAST_NAME\" as lastName, author.\"DOB\" as dob, author.\"DOD\" as dod, author.\"BIO\" as bio, "
				+ "genre.\"ID\" as genreId, genre.\"NAME\" as genreName, genre.\"DESCRIPTION\" as genreDescription "
				+ "FROM public.\"BOOK\" book "
				+ "LEFT JOIN public.\"AUTHOR\" author ON (book.\"AUTHOR_ID\" = author.\"ID\") "
				+ "LEFT JOIN public.\"GENRE\" genre ON (book.\"GENRE_ID\" = genre.\"ID\") "
				+ "WHERE book.\"ISBN\" = ?";
	}

	protected String prepareSql(String parameter) {
		if(parameter == null) { return prepareSql(); };
		return "SELECT "
				+ "book.\"ID\", book.\"ISBN\", book.\"TITLE\", book.\"DESCRIPTION\", "
				+ "book.\"AUTHOR_ID\" as authorId, author.\"FIRST_NAME\" as firstName, author.\"LAST_NAME\" as lastName, author.\"DOB\" as dob, author.\"DOD\" as dod, author.\"BIO\" as bio, "
				+ "genre.\"ID\" as genreId, genre.\"NAME\" as genreName, genre.\"DESCRIPTION\" as genreDescription "
				+ "FROM public.\"BOOK\" book "
				+ "LEFT JOIN public.\"AUTHOR\" author ON (book.\"AUTHOR_ID\" = author.\"ID\") "
				+ "LEFT JOIN public.\"GENRE\" genre ON (book.\"GENRE_ID\" = genre.\"ID\") "
				+ "WHERE book.\"" + parameter + "\" = ?";
	}

	protected String prepareChildrenSql() {
		return "SELECT \"ISBN\", \"TITLE\" FROM public.\"BOOK\"";
	}

}
