package fr.inria.shapedesigner;

import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.control.NamedQuery;
import fr.inria.shapedesigner.view.main.ConceptionTabController;
import fr.inria.shapedesigner.view.main.MainLayoutController;
import javafx.collections.ObservableList;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Main {
    protected CreateValidateProject project;
    protected File projectFile;
    public static void main(String[] args) {

        try {
            // At first get the required params
            //Choosing RDF SHACL project i.e. public void newSHACLRDFProject() { newRDFProject(PROJECT_TYPE.SHACL); }
            MainLayoutController mainLayoutController = new MainLayoutController();
            //args[0] is the address of the file
            mainLayoutController.newRDFProjectConsole(CreateValidateProject.PROJECT_TYPE.SHACL, args[0]);
            mainLayoutController.generateAllTypeQueries();

            ConceptionTabController conceptionTabController = new ConceptionTabController();
            ObservableList<NamedQuery> queries = mainLayoutController.getProject().getListOfQuery();
            queries.remove(0,1);
            System.out.println("Calling performAnalysis");
            mainLayoutController.getProject().performAnalysis(mainLayoutController.getProject().getListOfPattern().get(0),queries , conceptionTabController, args[1] );
            System.out.println("Saving project...");
            mainLayoutController.saveProjectHere(args[2]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
