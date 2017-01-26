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
        GridServiceManager gridServiceManager = admin.getGridServiceManagers().waitForAtLeastOne();

        // pu
        ProcessingUnitDeployment processingUnitDeployment = new ProcessingUnitDeployment(new File("path.jar"));
        processingUnitDeployment.name("pu-name");
        gridServiceManager.deploy(processingUnitDeployment);

        // app
        ApplicationDeployment applicationDeployment = new ApplicationDeployment("app-name");
        applicationDeployment.addProcessingUnitDeployment(new ProcessingUnitDeployment(new File("pu-1.jar")));
        applicationDeployment.addProcessingUnitDeployment(new ProcessingUnitDeployment(new File("pu-2.jar")));
        gridServiceManager.deploy(applicationDeployment);

        // undeploy pu
        admin.getProcessingUnits().waitFor("pu-name").undeployAndWait();

        // undeploy app
        admin.getApplications().waitFor("app-name").undeployAndWait();
    }
}
