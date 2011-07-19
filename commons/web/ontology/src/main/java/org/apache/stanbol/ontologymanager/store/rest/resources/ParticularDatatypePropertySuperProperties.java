package org.apache.stanbol.ontologymanager.store.rest.resources;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.stanbol.commons.web.base.ContextHelper;
import org.apache.stanbol.commons.web.base.resource.BaseStanbolResource;
import org.apache.stanbol.ontologymanager.store.api.LockManager;
import org.apache.stanbol.ontologymanager.store.api.PersistenceStore;
import org.apache.stanbol.ontologymanager.store.rest.LockManagerImp;
import org.apache.stanbol.ontologymanager.store.rest.ResourceManagerImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/ontology/{ontologyPath:.+}/datatypeProperties/{datatypePropertyPath:.+}/superProperties")
public class ParticularDatatypePropertySuperProperties extends BaseStanbolResource{
    private static final Logger logger = LoggerFactory
            .getLogger(ParticularDatatypePropertySuperProperties.class);

    private PersistenceStore persistenceStore;

    public ParticularDatatypePropertySuperProperties(@Context ServletContext context) {
        this.persistenceStore = ContextHelper.getServiceFromContext(PersistenceStore.class, context);
    }

    @POST
    public Response addSuperProperties(@PathParam("ontologyPath") String ontologyPath,
                                       @PathParam("datatypePropertyPath") String datatypePropertyPath,
                                       @FormParam("superPropertyURIs") List<String> superPropertyURIs) {
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainWriteLockFor(ontologyPath);
        try {

            ResourceManagerImp resourceManager = ResourceManagerImp.getInstance();
            String datatypePropertyURI = resourceManager.getResourceURIForPath(ontologyPath,
                datatypePropertyPath);
            if (datatypePropertyURI == null) {
                throw new WebApplicationException(Status.NOT_FOUND);
            } else {
                for (String superPropertyURI : superPropertyURIs) {
                    try {
                        persistenceStore.makeSubPropertyOf(datatypePropertyURI, superPropertyURI);
                    } catch (Exception e) {
                        throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        } finally {
            lockManager.releaseWriteLockFor(ontologyPath);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{superPropertyPath:.+}")
    public Response deleteSuperProperty(@PathParam("ontologyPath") String ontologyPath,
                                        @PathParam("datatypePropertyPath") String datatypePropertyPath,
                                        @PathParam("superPropertyPath") String superPropertyPath) {
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainWriteLockFor(ontologyPath);
        try {

            ResourceManagerImp resourceManager = ResourceManagerImp.getInstance();
            String datatypePropertyURI = resourceManager.getResourceURIForPath(ontologyPath,
                datatypePropertyPath);
            String superPropertyURI = resourceManager.convertEntityRelativePathToURI(superPropertyPath);
            if (datatypePropertyURI == null) {
                throw new WebApplicationException(Status.NOT_FOUND);
            } else {
                try {
                    persistenceStore.deleteSuperPropertyAssertion(datatypePropertyURI, superPropertyURI);
                } catch (Exception e) {
                    throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
                }
            }
        } finally {
            lockManager.releaseWriteLockFor(ontologyPath);
        }
        return Response.ok().build();

    }
}