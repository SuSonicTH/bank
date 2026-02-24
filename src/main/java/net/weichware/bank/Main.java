package net.weichware.bank;

import com.github.mvysny.vaadinboot.VaadinBoot;

public final class Main {
    public static final String APPLICATION_NAME = "Wolf Bank";

    public static void main(String[] args) throws Exception {
        VaadinBoot vaadinBoot = new VaadinBoot();
        vaadinBoot.setListenOn("localhost");
        vaadinBoot.setPort(4431);
        vaadinBoot.run();
    }
}
