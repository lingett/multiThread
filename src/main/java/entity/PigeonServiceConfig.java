package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PigeonServiceConfig {
    private int localPort;
    private int remotePort;
    private String serviceName;
    private String serviceUrl;
}
