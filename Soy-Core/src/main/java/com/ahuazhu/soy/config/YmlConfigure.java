package com.ahuazhu.soy.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.List;

/**
 * Created by zhuzhengwen on 2017/4/27.
 */
public class YmlConfigure implements Configure {

    private Executor executor;

    private boolean enablePollutionDefend;

    private List<InetAddress> tcpUpstream;

    private List<InetAddress> udpUpstream;


    private void init(String yamlFile) {

    }

    private static void init(File yamlFile) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        YmlConfigure configure = yaml.loadAs(new FileInputStream(yamlFile), YmlConfigure.class);

    }

    static class Executor {

        private String mode;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
            YmlConfigure.init(new File("/etc/soy/soy.yaml"));
    }


}

