import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.model.*;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.Shape;
import com.structurizr.view.ViewSet;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 */
public class AVS4HeathCheck {

    private static final long WORKSPACE_ID = 1;
    private static final String API_KEY = "c2022bfd-21d3-4d43-b97e-e14460d9db75";
    private static final String API_SECRET = "161757c1-8155-4d84-a976-e588a26d0e87";
    private static final String DATABASE_TAG = "Database";

    public static void main(String[] args) throws Exception {




            Workspace workspace = new Workspace("AVS4.0 health checks example", "AVS4.0 HelathCheck avec Structurizr On-Premise");

            //Workspace workspace = getClient().getWorkspace(1);

            Model model = workspace.getModel();
            ViewSet views = workspace.getViews();

            SoftwareSystem structurizr = model.addSoftwareSystem("AVS4.0_v3", "AVS4.0 Structurizr");

            Container consommateurContainer = structurizr.addContainer("ActiveMQ Comsommateur", "ActiveMQ Comsommateur", "SpringBoot");
            Container producteurContainer = structurizr.addContainer("ActiveMQ Producteur", "ActiveMQ Producteur", "SpringBoot");
            Container activeMQ = structurizr.addContainer("ActiveMQ", "ActiveMQ", "JMS");

        activeMQ.addTags(DATABASE_TAG);


            consommateurContainer.uses(activeMQ, "Lecture des événements");
            producteurContainer.uses(activeMQ,"Production des événements");

           // kibana.uses(elasticsearch, "Reads from and writes to", "TCP");

            DeploymentNode stackGlobale = model.addDeploymentNode("Localhost ActiveMQ-Test-V3", "Messaging", "localhost");
            //DeploymentNode pivotalWebServices = amazonWebServices.addDeploymentNode("Pivotal Web Services", "Platform as a Service provider.",
                                                                                     //"Cloud Foundry");

            ContainerInstance consumer = stackGlobale.addDeploymentNode("activemq-consumer", "Spring activemq JMS consommateur", "SpringBoot")
                                                                     .add(consommateurContainer);
            ContainerInstance producer = stackGlobale.addDeploymentNode("activemq-producer", "Spring activemq JMS producteur", "SpringBoot")
                                                                      .add(producteurContainer);

            ContainerInstance activejms = stackGlobale.addDeploymentNode("activemq", "ActiveMQ - JMS", "JMS")
                                                 .add(activeMQ);


            // add health checks to the container instances, which return a simple HTTP 200 to say everything is okay
            consumer.addHealthCheck("JMS Consommateur application UP", "http://localhost:8090/health");
            producer.addHealthCheck("JMS Producteur application UP", "http://localhost:8089/health");


         //   activejms.addHealthCheck("ActiveMQ UP","http://localhost:8161/api/message?destination=queue://q.test").addHeader
          //          ("Authorization","Basic YWRtaW46YWRtaW4=");


        activejms.addHealthCheck("ActiveMQ UP","http://localhost:8161").addHeader("Access-Control-Allow-Origin", "*");

            // the pass/fail status from the health checks is used to supplement any deployment views that include the container instances that have health checks defined
            DeploymentView deploymentView = views.createDeploymentView(structurizr, "Deployment-v3", "Diagramme déploiement");
            deploymentView.setEnvironment("Localhost - SCE");
            deploymentView.addAllDeploymentNodes();

            views.getConfiguration().getStyles().addElementStyle(Tags.ELEMENT).color("#ffffff");
            views.getConfiguration().getStyles().addElementStyle(DATABASE_TAG).shape(Shape.Pipe);
        views.getConfiguration().getStyles().addElementStyle(DATABASE_TAG).width(600);
        views.getConfiguration().getStyles().addElementStyle(DATABASE_TAG).height(150);

            //StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
            //structurizrClient.putWorkspace(WORKSPACE_ID, workspace);

            uploadWorkspaceToStructurizr(workspace);

    }

    private static void uploadWorkspaceToStructurizr(Workspace workspace) throws Exception {
        StructurizrClient structurizrClient = getClient();
        structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
    }

    private static StructurizrClient getClient(){
        return new StructurizrClient("http://localhost:8080/api",API_KEY, API_SECRET);
    }

}