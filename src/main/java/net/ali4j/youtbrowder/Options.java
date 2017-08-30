package net.ali4j.youtbrowder;

/**
 * Created by ehsan on 8/30/2017.
 */
public class Options {

    private String port;
    private String host;
    private Boolean useProxy;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getUseProxy() {
        return useProxy;
    }

    public void setUseProxy(Boolean useProxy) {
        this.useProxy = useProxy;
    }
}
