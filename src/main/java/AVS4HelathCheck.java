import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.*;
import com.structurizr.view.*;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 */
public class AVS4HelathCheck {

    private static final long WORKSPACE_ID = 1;
    private static final String API_KEY = "c2022bfd-21d3-4d43-b97e-e14460d9db75";
    private static final String API_SECRET = "161757c1-8155-4d84-a976-e588a26d0e87";
    private static final String DATABASE_TAG = "Database";

    public static void main(String[] args) throws Exception {




            Workspace workspace = new Workspace("HTTP-based health checks example", "An example of how to use the HTTP-based health checks feature");
            Model model = workspace.getModel();
            ViewSet views = workspace.getViews();

            SoftwareSystem structurizr = model.addSoftwareSystem("Structurizr", "A publishing platform for software architecture diagrams and documentation based upon the C4 model.");
            Container webApplication = structurizr.addContainer("structurizr.com", "Provides all of the server-side functionality of Structurizr, serving static and dynamic content to users.", "Java and Spring MVC");
            Container database = structurizr.addContainer("Database", "Stores information about users, workspaces, etc.", "Relational Database Schema");
            database.addTags(DATABASE_TAG);
            webApplication.uses(database, "Reads from and writes to", "JDBC");

            DeploymentNode amazonWebServices = model.addDeploymentNode("Amazon Web Services", "", "us-east-1");
            DeploymentNode pivotalWebServices = amazonWebServices.addDeploymentNode("Pivotal Web Services", "Platform as a Service provider.", "Cloud Foundry");
            ContainerInstance liveWebApplication = pivotalWebServices.addDeploymentNode("www.structurizr.com", "An open source Java EE web server.", "Apache Tomcat")
                                                                     .add(webApplication);
            ContainerInstance liveDatabaseInstance = amazonWebServices.addDeploymentNode("Amazon RDS", "Database as a Service provider.", "MySQL")
                                                                      .add(database);

            // add health checks to the container instances, which return a simple HTTP 200 to say everything is okay
            liveWebApplication.addHealthCheck("Web Application is running", "https://www.structurizr.com/health");
            liveDatabaseInstance.addHealthCheck("Database is accessible from Web Application", "https://www.structurizr.com/health/database");

            // the pass/fail status from the health checks is used to supplement any deployment views that include the container instances that have health checks defined
            DeploymentView deploymentView = views.createDeploymentView(structurizr, "Deployment", "A deployment diagram showing the live environment.");
            deploymentView.setEnvironment("Live");
            deploymentView.addAllDeploymentNodes();

            views.getConfiguration().getStyles().addElementStyle(Tags.ELEMENT).color("#ffffff");
            views.getConfiguration().getStyles().addElementStyle(DATABASE_TAG).shape(Shape.Cylinder);

            //StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
            //structurizrClient.putWorkspace(WORKSPACE_ID, workspace);

            uploadWorkspaceToStructurizr(workspace);

    }

    private static void uploadWorkspaceToStructurizr(Workspace workspace) throws Exception {
        StructurizrClient structurizrClient = new StructurizrClient("http://localhost:8080/api",API_KEY, API_SECRET);
        structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
    }

}