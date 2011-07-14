/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.stanbol.rules.web.resources;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.stanbol.commons.web.base.ContextHelper;
import org.apache.stanbol.commons.web.base.format.KRFormat;
import org.apache.stanbol.commons.web.base.resource.BaseStanbolResource;
import org.apache.stanbol.ontologymanager.ontonet.api.ONManager;
import org.apache.stanbol.ontologymanager.ontonet.impl.ONManagerImpl;
import org.apache.stanbol.ontologymanager.ontonet.impl.io.ClerezzaOntologyStorage;
import org.apache.stanbol.rules.base.api.RuleStore;
import org.apache.stanbol.rules.manager.changes.AddRecipe;
import org.apache.stanbol.rules.manager.changes.GetRecipe;
import org.apache.stanbol.rules.manager.changes.RemoveRecipe;
import org.apache.stanbol.rules.manager.changes.RuleStoreImpl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author elvio, andrea.nuzzolese
 */
@Path("/recipe")
// /{uri:.+}")
// @ImplicitProduces(MediaType.TEXT_HTML + ";qs=2")
public class RecipeResource extends BaseStanbolResource {

    protected ONManager onm;

    private Logger log = LoggerFactory.getLogger(getClass());

    private RuleStoreImpl kresRuleStore;
    private ClerezzaOntologyStorage storage;

    /**
     * To get the RuleStoreImpl where are stored the rules and the recipes
     * 
     * @param servletContext
     *            {To get the context where the REST service is running.}
     */
    public RecipeResource(@Context ServletContext servletContext) {
    	this.kresRuleStore = (RuleStoreImpl) ContextHelper.getServiceFromContext(RuleStore.class, servletContext);
        this.onm = (ONManager) ContextHelper.getServiceFromContext(ONManager.class, servletContext);
        this.storage = onm.getOntologyStore();
    }

    /**
     * Get a recipe with its rules from the rule base (that is the ontology that contains the rules and the
     * recipe).
     * 
     * @param uri
     *            {A string contains the IRI full name of the recipe.}
     * @return Return: <br/>
     *         200 The recipe is retrieved (import declarations point to KReS Services) <br/>
     *         404 The recipe does not exists in the manager <br/>
     *         500 Some error occurred
     * 
     */
    @GET
    @Path("/{uri:.+}")
    @Produces(value = {KRFormat.RDF_XML, KRFormat.RDF_JSON, KRFormat.OWL_XML, KRFormat.FUNCTIONAL_OWL,KRFormat.MANCHESTER_OWL, KRFormat.TURTLE})
    public Response getRecipe(@PathParam("uri") String uri) throws OWLOntologyCreationException {
        try {
        
            GetRecipe rule = new GetRecipe(kresRuleStore);;    
            // String ID =
            // kresRuleStore.getOntology().getOntologyID().toString().replace(">","").replace("<","")+"#";

            if (uri.equals("all")) {
                Vector<IRI> recipe = rule.getGeneralRecipes();
                if (recipe == null) {
                    // The recipe does not exists in the manager
                    return Response.status(Status.NOT_FOUND).build();
                } else {

                    // The recipe is retrieved (import declarations point to
                    // KReS Services)
                    OWLOntology onto = kresRuleStore.getOntology();
                    OWLOntology newmodel = OWLManager.createOWLOntologyManager().createOntology(
                        onto.getOntologyID());
                    OWLDataFactory factory = onto.getOWLOntologyManager().getOWLDataFactory();

                    Iterator<OWLOntology> importedonto = onto.getDirectImports().iterator();
                    List<OWLOntologyChange> additions = new LinkedList<OWLOntologyChange>();
                    OWLDataFactory auxfactory = onto.getOWLOntologyManager().getOWLDataFactory();

                    while (importedonto.hasNext()) {
                        OWLOntology auxonto = importedonto.next();
                        additions.add(new AddImport(newmodel, auxfactory.getOWLImportsDeclaration(auxonto
                                .getOWLOntologyManager().getOntologyDocumentIRI(auxonto))));
                    }

                    if (!additions.isEmpty()) newmodel.getOWLOntologyManager().applyChanges(additions);

                    for (int i = 0; i < recipe.size(); i++) {
                        OWLNamedIndividual ind = factory.getOWLNamedIndividual(recipe.get(i));
                        Set<OWLIndividualAxiom> ax = onto.getAxioms(ind);
                        newmodel.getOWLOntologyManager().addAxioms(newmodel, ax);

                    }

                    // try {
                    // OWLManager.createOWLOntologyManager().saveOntology(
                    // newmodel,
                    // newmodel.getOWLOntologyManager()
                    // .getOntologyFormat(newmodel),
                    // System.out);
                    // } catch (OWLOntologyStorageException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }
                    return Response.ok(newmodel).build();
                }

            } else {

                HashMap<IRI,String> recipe = rule.getRecipe(IRI.create(uri));

                if (recipe == null) {
                    // The recipe deos not exists in the manager
                    return Response.status(Status.NOT_FOUND).build();
                } else {
                    // The recipe is retrieved (import declarations point to
                    // KReS Services)
                    OWLOntology onto = kresRuleStore.getOntology();

                    OWLDataFactory factory = onto.getOWLOntologyManager().getOWLDataFactory();
                    OWLObjectProperty prop = factory.getOWLObjectProperty(IRI
                            .create("http://kres.iks-project.eu/ontology/meta/rmi.owl#hasRule"));
                    OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(uri));
                    Set<OWLIndividual> value = ind.getObjectPropertyValues(prop, onto);
                    Set<OWLIndividualAxiom> ax = onto.getAxioms(ind);

                    Iterator<OWLIndividual> iter = value.iterator();

                    OWLOntology newmodel = OWLManager.createOWLOntologyManager().createOntology(
                        onto.getOntologyID());

                    Iterator<OWLOntology> importedonto = onto.getDirectImports().iterator();
                    List<OWLOntologyChange> additions = new LinkedList<OWLOntologyChange>();
                    OWLDataFactory auxfactory = onto.getOWLOntologyManager().getOWLDataFactory();

                    while (importedonto.hasNext()) {
                        OWLOntology auxonto = importedonto.next();
                        additions.add(new AddImport(newmodel, auxfactory.getOWLImportsDeclaration(auxonto
                                .getOWLOntologyManager().getOntologyDocumentIRI(auxonto))));
                    }

                    if (!additions.isEmpty()) newmodel.getOWLOntologyManager().applyChanges(additions);

                    newmodel.getOWLOntologyManager().addAxioms(newmodel, ax);

                    while (iter.hasNext()) {

                        ind = (OWLNamedIndividual) iter.next();
                        ax = onto.getAxioms(ind);

                        newmodel.getOWLOntologyManager().addAxioms(newmodel, ax);
                    }

                    // try {
                    // OWLManager.createOWLOntologyManager().saveOntology(
                    // newmodel,
                    // newmodel.getOWLOntologyManager()
                    // .getOntologyFormat(newmodel),
                    // System.out);
                    // } catch (OWLOntologyStorageException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }

                    return Response.ok(newmodel).build();
                }
            }
        } catch (Exception e) {
            // Some error occurred
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * To add a recipe without rules.
     * 
     * @param recipe
     *            {A string contains the IRI of the recipe to be added}
     * @param description
     *            {A string contains a description of the rule}
     * @return Return: <br/>
     *         200 The recipe has been added<br/>
     *         409 The recipe has not been added<br/>
     *         500 Some error occurred
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(value = {KRFormat.RDF_XML, KRFormat.TURTLE, KRFormat.OWL_XML, KRFormat.FUNCTIONAL_OWL,KRFormat.MANCHESTER_OWL, KRFormat.RDF_JSON})
    public Response addRecipe(@FormParam(value = "recipe") String recipe, @FormParam(value = "description") String description) {

        try {

            AddRecipe instance = new AddRecipe(kresRuleStore);

            // String ID =
            // kresRuleStore.getOntology().getOntologyID().toString().replace(">","").replace("<","")+"#";

            boolean ok = instance.addSimpleRecipe(IRI.create(recipe), description);

            if (!ok) {

                return Response.status(Status.CONFLICT).build();

            } else {
                kresRuleStore.saveOntology();
                return Response.status(Status.OK).build();
            }

        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To delete a recipe
     * 
     * @param recipe
     *            {A tring contains an IRI of the recipe}
     * @return 200 The recipe has been deleted<br/>
     *         409 The recipe has not been deleted<br/>
     *         500 Some error occurred
     */
    @DELETE
    // @Consumes(MediaType.TEXT_PLAIN)
    @Produces("text/plain")
    public Response removeRecipe(@QueryParam(value = "recipe") String recipe) {

        try {

            RemoveRecipe instance = new RemoveRecipe(kresRuleStore);

            // String ID =
            // kresRuleStore.getOntology().getOntologyID().toString().replace(">","").replace("<","")+"#";

            boolean ok = instance.removeRecipe(IRI.create(recipe));

            if (!ok) {
                return Response.status(Status.CONFLICT).build();
            } else {
                kresRuleStore.saveOntology();
                return Response.ok().build();
            }

        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

}