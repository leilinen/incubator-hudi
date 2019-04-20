package com.uber.hoodie.utilities.sources.decoder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class HoodieHdfsProperties {
 private static Logger logger = LogManager.getLogger(HoodieHdfsProperties.class);

    private Properties props;

    private static String DFS_NAMESERVICES = "dfs.nameservices";
    private static String DFS_HA_NAMENODES = "dfs.ha.namenodes";
    private static String DFS_NAMENODE_RPC_ADDRESS = "dfs.namenode.rpc-address";
    private static String DFS_CLIENT_PROXY_PROVIDER = "dfs.client.failover.proxy.provider";
    private static String FS_DEFAULT_FS = "fs.defaultFS";
    private static String HDFS_USER = "hdfs.user";

    HoodieHdfsProperties(Properties props) {
        this.props = props;
    }

    public String getNameServices() {
        return this.props.getProperty(DFS_NAMESERVICES);
    }

    public String getNamenodes() {
        return this.props.getProperty(DFS_HA_NAMENODES);
    }

    public String getNamenodesAdress() {
        return this.props.getProperty(DFS_NAMENODE_RPC_ADDRESS);
    }

    public String getClientProxyProvider() {
        return this.props.getProperty(DFS_CLIENT_PROXY_PROVIDER);
    }

    public String getDefaultFs() {
        return this.props.getProperty(FS_DEFAULT_FS);
    }

    public String getHdfsUser() {
        return this.props.getProperty(HDFS_USER);
    }

    public FileSystem getFileSystem() {
        Configuration conf = new Configuration();
        FileSystem fileSystem;

        conf.set(DFS_NAMESERVICES, getNameServices());
        conf.set(DFS_HA_NAMENODES.concat(getNameServices()), getNamenodes());
        conf.set(DFS_CLIENT_PROXY_PROVIDER.concat(getNameServices()), getClientProxyProvider());
        conf.set(FS_DEFAULT_FS, getDefaultFs());

        String[] namenodesArr = getNamenodes().split(",");
        String[] namenodesAdessArr = getNamenodesAdress().split(",");
        for (int i=0; i<namenodesArr.length; i++) {
            conf.set(DFS_NAMENODE_RPC_ADDRESS.concat(getNameServices()).concat(".").concat(namenodesAdessArr[i]), namenodesAdessArr[i]);
        }
        logger.info("fs configuration: " + conf.toString());
        try {
            fileSystem = FileSystem.get(new URI(getDefaultFs()), conf, getHdfsUser()) ;
            return fileSystem;
        } catch (InterruptedException | IOException | URISyntaxException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
