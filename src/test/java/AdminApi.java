import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.alert.config.AlertConfiguration;
import org.openspaces.admin.alert.config.CpuUtilizationAlertConfiguration;

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
}