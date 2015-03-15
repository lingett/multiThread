package common;

import com.dianping.dpsf.api.ProxyFactory;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import entity.FutureResult;
import entity.PigeonServiceConfig;
import entity.SshConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
@NoArgsConstructor
public abstract class AbstractPigeonTest<Service, Result extends FutureResult> extends AbstractPressureTest<Result> {
    private JSch jsch;
    private Session session;
    private Service service;
    private SshConfig sshConfig = new SshConfig();
    private PigeonServiceConfig pigeonServiceConfig;

    protected AbstractPigeonTest(int poolSize, int cycleTimes, PigeonServiceConfig pigeonServiceConfig) {
        super(poolSize, cycleTimes);
        this.pigeonServiceConfig = pigeonServiceConfig;
        sshConnect();
        service = getServiceProxy();
    }

    @Override
    protected void done() {
        super.done();
        sshDisconnect();
    }

    private void sshConnect() {
        try {
            jsch = new JSch();
            session = jsch.getSession(sshConfig.getUsername(), sshConfig.getHost());
            session.setPassword(sshConfig.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            session.setPortForwardingL(pigeonServiceConfig.getLocalPort(), pigeonServiceConfig.getServiceUrl(), pigeonServiceConfig.getRemotePort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sshDisconnect() {
        if (null != session) {
            session.disconnect();
        }
    }

    private Service getServiceProxy() {
        ProxyFactory<Service> proxyFactory = new ProxyFactory<Service>();
        proxyFactory.setUseLion(false);
        proxyFactory.setIface(getServiceClass());
        proxyFactory.setServiceName(pigeonServiceConfig.getServiceName());
        proxyFactory.setHosts("127.0.0.1:" + pigeonServiceConfig.getLocalPort());
        proxyFactory.setUseLion(false);
        proxyFactory.setSerialize("hessian");
        proxyFactory.setCallMethod("sync");
        proxyFactory.setTimeout(5000);
        proxyFactory.init();
        try {
            return proxyFactory.getProxy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Class<Service> getServiceClass() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return (Class<Service>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new RuntimeException("没有找到类信息");
    }
}
