import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.alert.config.AlertConfiguration;
import org.openspaces.admin.alert.config.CpuUtilizationAlertConfiguration;
import org.openspaces.admin.application.ApplicationDeployment;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnitDeployment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminApi {

    @Test
    public void alerts() {
        Admin admin = new AdminFactory().create();
        List<AlertConfiguration> configurations = new ArrayList<>();
        AlertConfiguration configuration = new CpuUtilizationAlertConfiguration();
        configuration.setEnabled(true);
        configuration.setProperties(ImmutableMap.of(
                "measurement-period-milliseconds", "5000",
                "low-threshold-perc", "10",
                "high-threshold-perc", "80"));
        configurations.add(configuration);
        admin.getAlertManager().configure(configurations.toArray(new AlertConfiguration[configurations.size()]));
    }

    public void deployment() {
        Admin admin = new AdminFactory().create();
        GridServiceManager gsm = admin.getGridServiceManagers().waitForAtLeastOne();

        // deploy pu
        ProcessingUnitDeployment puDeployment = new ProcessingUnitDeployment(new File("path.jar"));
        puDeployment.name("pu-name");
        puDeployment.addZone("zone-1");
        puDeployment.setContextProperty("key1", "value1");
        puDeployment.setContextProperty("key2", "value2");
        gsm.deploy(puDeployment);

        // undeploy pu
        admin.getProcessingUnits().waitFor("pu-name").undeployAndWait();


        // deploy app
        ApplicationDeployment appDeployment = new ApplicationDeployment("app-name");
        // pu-1
        ProcessingUnitDeployment puDeployment1 = new ProcessingUnitDeployment(new File("pu-1.jar"));
        puDeployment1.name("pu-name-1");
        appDeployment.addProcessingUnitDeployment(puDeployment1);
        // pu-2, depends on pu-1
        ProcessingUnitDeployment puDeployment2 = new ProcessingUnitDeployment(new File("pu-2.jar"));
        puDeployment2.name("pu-name-2");
        puDeployment1.addDependency("pu-name-1");
        appDeployment.addProcessingUnitDeployment(puDeployment2);
        gsm.deploy(appDeployment);

        // undeploy app
        admin.getApplications().waitFor("app-name").undeployAndWait();
    }
}
